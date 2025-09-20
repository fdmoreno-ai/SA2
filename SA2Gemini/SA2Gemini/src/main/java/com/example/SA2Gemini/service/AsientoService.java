package com.example.SA2Gemini.service;

import com.example.SA2Gemini.dto.LibroMayorReportData;
import com.example.SA2Gemini.entity.*;
import com.example.SA2Gemini.repository.AsientoRepository;
import com.example.SA2Gemini.repository.CuentaRepository;
import com.example.SA2Gemini.repository.MovimientoRepository;
import com.example.SA2Gemini.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AsientoService {

    @Autowired
    private AsientoRepository asientoRepository;

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public void saveAsiento(Asiento asiento) throws Exception {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new Exception("Usuario no autenticado.");
        }
        String username = authentication.getName();
        Usuario currentUser = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("Usuario autenticado no encontrado en la base de datos."));

        asiento.setUsuarioCreador(currentUser);

        if (asiento.getMovimientos() == null || asiento.getMovimientos().isEmpty()) {
            throw new Exception("Un asiento debe tener al menos un movimiento.");
        }

        if (asiento.getMovimientos().size() < 2) {
            throw new Exception("No se pueden agregar asientos que tengan solo un movimiento.");
        }

        BigDecimal totalDebe = BigDecimal.ZERO;
        BigDecimal totalHaber = BigDecimal.ZERO;

        // Chronology Rule: New asiento date must be equal to or later than the last asiento date.
        asientoRepository.findTopByOrderByFechaDesc().ifPresent(lastAsiento -> {
            if (asiento.getFecha().isBefore(lastAsiento.getFecha())) {
                throw new RuntimeException("La fecha del asiento no puede ser anterior a la del último asiento registrado (" + lastAsiento.getFecha() + ").");
            }
        });

        // Future Date Rule: Asiento date cannot be in the future.
        if (asiento.getFecha().isAfter(LocalDate.now())) {
            throw new RuntimeException("La fecha del asiento no puede ser una fecha futura.");
        }

        Set<Long> debitAccountIds = new HashSet<>();
        Set<Long> creditAccountIds = new HashSet<>();

        for (Movimiento movimiento : asiento.getMovimientos()) {
            if (movimiento.getCuenta() == null || movimiento.getCuenta().getId() == null) {
                throw new Exception("Todos los movimientos deben tener una cuenta asociada.");
            }

            boolean hasDebe = movimiento.getDebe() != null && movimiento.getDebe().compareTo(BigDecimal.ZERO) > 0;
            boolean hasHaber = movimiento.getHaber() != null && movimiento.getHaber().compareTo(BigDecimal.ZERO) > 0;

            if (!hasDebe && !hasHaber) {
                throw new Exception("Cada movimiento debe tener un monto en el Debe o en el Haber.");
            }
            if (hasDebe && hasHaber) {
                throw new Exception("Un movimiento no puede tener monto en el Debe y en el Haber simultáneamente.");
            }

            if (hasDebe) {
                totalDebe = totalDebe.add(movimiento.getDebe());
                debitAccountIds.add(movimiento.getCuenta().getId());
            } else {
                totalHaber = totalHaber.add(movimiento.getHaber());
                creditAccountIds.add(movimiento.getCuenta().getId());
            }
        }

        // Check for accounts that are both debited and credited in the same asiento
        for (Long debitAccountId : debitAccountIds) {
            if (creditAccountIds.contains(debitAccountId)) {
                throw new Exception("Una cuenta no puede ser debitada y acreditada en el mismo asiento.");
            }
        }

        if (totalDebe.compareTo(totalHaber) != 0) {
            throw new Exception("El total del debe no coincide con el total del haber.");
        }

        if (totalDebe.compareTo(BigDecimal.ZERO) == 0 && totalHaber.compareTo(BigDecimal.ZERO) == 0) {
            throw new Exception("El asiento no puede tener montos totales en cero.");
        }

        // Set the back-reference from Movimiento to Asiento
        for (Movimiento movimiento : asiento.getMovimientos()) {
            movimiento.setAsiento(asiento);
        }

        asientoRepository.save(asiento);
    }

    public List<Asiento> getAsientosBetweenDates(LocalDate startDate, LocalDate endDate) {
        return asientoRepository.findByFechaBetween(startDate, endDate);
    }

    public BigDecimal getAccountBalanceUpToDate(Cuenta cuenta, LocalDate date) {
        List<Movimiento> movementsBeforeDate = movimientoRepository.findByCuentaAndAsiento_FechaBeforeOrderByFechaAscIdAsc(cuenta, date);
        BigDecimal balance = BigDecimal.ZERO;
        for (Movimiento mov : movementsBeforeDate) {
            if (mov.getDebe() != null) {
                balance = balance.add(mov.getDebe());
            }
            if (mov.getHaber() != null) {
                balance = balance.subtract(mov.getHaber());
            }
        }
        return balance;
    }

    

    public Map<String, List<LibroMayorReportData>> generarLibroMayorReporte(LocalDate fechaInicio, LocalDate fechaFin, Long cuentaId) {
        System.out.println("generarLibroMayorReporte called with: fechaInicio=" + fechaInicio + ", fechaFin=" + fechaFin + ", cuentaId=" + cuentaId);
        Map<String, List<LibroMayorReportData>> reportePorCuenta = new LinkedHashMap<>(); // Use LinkedHashMap to maintain insertion order

        List<Cuenta> cuentas;
        if (cuentaId != null) {
            cuentas = cuentaRepository.findById(cuentaId)
                                      .filter(Cuenta::isActivo) // Ensure the selected account is active
                                      .map(List::of)
                                      .orElse(new ArrayList<>());
        } else {
            cuentas = cuentaRepository.findByActivoTrue(); // Only fetch active accounts
        }

        for (Cuenta cuenta : cuentas) {
            List<LibroMayorReportData> movimientosCuenta = new ArrayList<>();
            BigDecimal saldoActual = getAccountBalanceUpToDate(cuenta, fechaInicio); // Start with initial balance

            // Fetch movements for the current account within the date range
            List<Movimiento> movimientos = movimientoRepository.findByCuentaAndAsiento_FechaBetween(cuenta, fechaInicio, fechaFin);

            // Sort movements by date and then by asiento ID for consistent chronological order
            movimientos.sort(Comparator
                    .comparing((Movimiento mov) -> mov.getAsiento().getFecha())
                    .thenComparing(mov -> mov.getAsiento().getId())); // Assuming Asiento has an ID for secondary sort

            for (Movimiento movimiento : movimientos) {
                LibroMayorReportData data = new LibroMayorReportData();
                data.setFecha(movimiento.getAsiento().getFecha());
                data.setDetalle(movimiento.getAsiento().getDescripcion());
                data.setDebe(movimiento.getDebe());
                data.setHaber(movimiento.getHaber());

                // Calculate cumulative balance based on TipoCuenta
                TipoCuenta tipoCuenta = cuenta.getTipoCuenta();
                if (tipoCuenta == null) {
                    // Default to ACTIVO logic if tipoCuenta is null (e.g., for old accounts)
                    saldoActual = saldoActual.add(movimiento.getDebe()).subtract(movimiento.getHaber());
                } else {
                    switch (tipoCuenta) {
                        case ACTIVO:
                        case RESULTADO_NEGATIVO:
                            saldoActual = saldoActual.add(movimiento.getDebe()).subtract(movimiento.getHaber());
                            break;
                        case PASIVO:
                        case PATRIMONIO:
                        case RESULTADO_POSITIVO:
                            saldoActual = saldoActual.subtract(movimiento.getDebe()).add(movimiento.getHaber());
                            break;
                    }
                }
                data.setSaldo(saldoActual);

                movimientosCuenta.add(data);
            }
            // Only add accounts that have movements within the report period or an initial balance
            if (!movimientosCuenta.isEmpty() || saldoActual.compareTo(BigDecimal.ZERO) != 0) {
                reportePorCuenta.put(cuenta.getNombre(), movimientosCuenta);
            }
        }

        return reportePorCuenta;
    }
}
