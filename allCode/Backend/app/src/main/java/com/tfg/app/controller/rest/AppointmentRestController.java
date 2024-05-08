package com.tfg.app.controller.rest;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import java.net.URI;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.tfg.app.controller.DTOS.AppointmentDTO;
import com.tfg.app.model.Appointment;
import com.tfg.app.model.Description;
import com.tfg.app.model.User;
import com.tfg.app.repository.AppointmentRepository;
import com.tfg.app.service.AppointmentService;
import com.tfg.app.service.DescriptionService;
import com.tfg.app.service.UserService;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentRestController {

    @Autowired
    private UserService userService;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private DescriptionService descriptionService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    User currentUser;

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            userService.findByEmail(principal.getName()).ifPresent(us -> currentUser = us);
            model.addAttribute("curretUser", currentUser);

        } else {
            model.addAttribute("logged", false);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        List<Appointment> appointmentList = appointmentService.findAll();
        return ResponseEntity.ok(appointmentList);
    }

    @PostMapping("/")
    public ResponseEntity<Appointment> addAppointment(@RequestBody AppointmentDTO appointmentDTO) {
        Appointment appointment = new Appointment(appointmentDTO);
        appointment.setUser(currentUser);
        appointment.setInterventions(new ArrayList<>());
        appointment.setCodEntity(currentUser.getCodEntity());
        appointmentService.save(appointment);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(appointment.getId()).toUri();
        return ResponseEntity.created(location).body(appointment);
    }

    @PostMapping("/user={userId}")
    public ResponseEntity<Appointment> addAppointmentToUser(@RequestBody AppointmentDTO appointmentDTO,
            @PathVariable("userId") Long userId) {
        Appointment appointment = new Appointment(appointmentDTO);
        User user = userService.findById(userId).orElseThrow();
        if (user != null) {
            appointment.setUser(user);
            if (user.getCodEntity().equals(currentUser.getCodEntity()))
                appointment.setCodEntity(user.getCodEntity());
            else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Las entidades de código no coinciden.");
        }
        appointment.setCompleted(false);
        appointment.setInterventions(new ArrayList<>());
        // appointment.setDoctorAsignated(currentUser);

        appointmentService.save(appointment);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(appointment.getId()).toUri();
        return ResponseEntity.created(location).body(appointment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Appointment>> getAppointment(@PathVariable Long id) {
        Optional<Appointment> appointment = appointmentService.findById(id);
        return ResponseEntity.ok(appointment);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable Long id,
            @RequestParam(value = "completed") boolean completed) {
        Appointment appointment = appointmentService.findById(id).orElseThrow();
        if (appointment == null) {
            return ResponseEntity.notFound().build();
        } else {
            appointment.setCompleted(completed);

            Appointment finishAppointment = appointmentRepository.save(appointment);
            return ResponseEntity.ok(finishAppointment);
        }
    }

    @PutMapping("/fullupdate/{id}")
    public ResponseEntity<?> updateAppointment(@PathVariable Long id,
            @RequestParam(value = "bookDate", required = false) String bookDate,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "additionalNote", required = false) String additionalNote,
            @RequestParam(value = "doctorAsignatedId", required = false) String doctorAsignatedId) {

        Appointment appointment = appointmentService.findById(id).orElseThrow();
        if (bookDate != null) {
            appointment.setBookDate(LocalDate.parse(bookDate));
        }
        if (fromDate != null) {
            appointment.setFromDate(LocalTime.parse(fromDate));
        }
        if (toDate != null) {
            appointment.setToDate(LocalTime.parse(toDate));
        }
        if (description != null) {
            appointment.setDescription(description);
        }
        if (additionalNote != null) {
            appointment.setAdditionalNote(additionalNote);
        }
        if (doctorAsignatedId != null) {
            User doctor = userService.findById(Long.parseLong(doctorAsignatedId)).orElseThrow();
            if (doctor != null) {
                appointment.setDoctorAsignated(doctor);
            }
        }
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return ResponseEntity.ok(updatedAppointment);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id) {
        try {
            appointmentService.delete(id);
            return ResponseEntity.ok("Appointment eliminado con éxito.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el Appointment: " + e.getMessage());
        }
    }

    @GetMapping("/all/{id}")
    public ResponseEntity<List<Appointment>> getAppointmentByUser(@PathVariable Long id) {
        User user = userService.findById(id).orElseThrow();
        if (user != null) {
            List<Appointment> appointments = appointmentService.getAllAppointmentsByUserId(id);
            return ResponseEntity.ok(appointments);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("byAppointment/{idIntervention}")
    public ResponseEntity<Appointment> getAppointmentByInterventionId(
            @PathVariable("idIntervention") Long idIntervention) {
        return ResponseEntity.ok().body(appointmentService.getAppointmentByInterventionId(idIntervention));
    }

    @GetMapping("/search")
    public List<Appointment> searchUsersByNameOrLastName(@RequestParam String query) {
        return appointmentService.findAppointmentsByUserDetails(query, query, query);
    }

    @PostMapping("/check-availability")
    public ResponseEntity<Boolean> checkAppointmentAvailability(@RequestBody AvailabilityCheckRequest request) {
        List<Appointment> appointments = appointmentService
                .findAllAppointmentsByDoctorAsignatedId(request.getDoctorId());
        boolean isDoctorAvailable = true;
        for (Appointment appointment : appointments) {
            if (appointment.getBookDate().isEqual(request.getBookDate()) &&
                    !(appointment.getFromDate().isAfter(request.getToDate()) ||
                            appointment.getToDate().isBefore(request.getFromDate()))) {
                // Si hay una superposición, el doctor no está disponible
                return ResponseEntity.ok(false);
            }
        }

        return ResponseEntity.ok(isDoctorAvailable);
    }

    @GetMapping("/all-description")
    public ResponseEntity<List<Description>> getAllDescriptions() {
        List<Description> descriptions = descriptionService.findAll();
        return ResponseEntity.ok(descriptions);
    }

    @GetMapping("/appointment/{codEntity}")
    public ResponseEntity<List<Appointment>> getAppointmentByCodEntity(@PathVariable("codEntity") Long codEntity) {
        List<Appointment> appointments = appointmentService.getAllAppointmentsByCodEntity(codEntity);
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }
}

class AvailabilityCheckRequest {
    private LocalDate bookDate;
    private LocalTime fromDate;
    private LocalTime toDate;
    private Long doctorId;

    public LocalTime getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalTime fromDate) {
        this.fromDate = fromDate;
    }

    public LocalTime getToDate() {
        return toDate;
    }

    public void setToDate(LocalTime toDate) {
        this.toDate = toDate;
    }

    public LocalDate getBookDate() {
        return bookDate;
    }

    public void setBookDate(LocalDate bookDate) {
        this.bookDate = bookDate;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

}