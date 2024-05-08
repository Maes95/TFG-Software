package com.tfg.app.controller.rest;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.tfg.app.controller.DTOS.UserDTO;
import com.tfg.app.controller.DTOS.UserDoctorDTO;
import com.tfg.app.controller.DTOS.UserEditDTO;
import com.tfg.app.model.Appointment;
import com.tfg.app.model.User;
import com.tfg.app.model.Util;
import com.tfg.app.repository.UserRepository;
import com.tfg.app.service.AppointmentService;
import com.tfg.app.service.UserService;
import com.tfg.app.service.UtilService;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    @Autowired
    private UserService userService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Environment env;

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

    @GetMapping("/me")
    public ResponseEntity<User> me(HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            return ResponseEntity.ok(userService.findByEmail(principal.getName()).orElseThrow());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> register(@RequestBody UserDTO userDTO) throws NotFoundException {
        User user = new User(userDTO);

        if (!userService.existUsername(user.getUsername())) {
            String imageUrl = "/static/avatar/predAvatar.png";
            try {
                Resource image = new ClassPathResource(imageUrl);
                user.setProfileAvatarFile(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            user.setPasswordEncoded(passwordEncoder.encode(user.getPasswordEncoded()));
            user.setDoctorAsignated(currentUser);
            user.setRoles(List.of("USER"));
            user.setCodEntity(currentUser.getCodEntity());

            userService.save(user);
            if (!Arrays.asList(env.getActiveProfiles()).contains("test")) {
                int totalAux = utilService.getNumPatientsTotal() + 1;
                System.out.println("//////////////////////////////////////" + totalAux);
                Util utilAux = new Util(0, 0, totalAux);
                utilService.partialUpdate(23L, utilAux);
            }
            URI location = fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();

            return ResponseEntity.created(location).body(user);
        } else {
            return new ResponseEntity<User>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/doctor")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> registerDoctor(@RequestBody UserDoctorDTO UserDoctorDTO) throws NotFoundException {
        User user = new User(UserDoctorDTO);

        if (!userService.existUsername(user.getUsername())) {
            String imageUrl = "/static/avatar/predAdminAvatar.png";
            try {
                Resource image = new ClassPathResource(imageUrl);
                user.setProfileAvatarFile(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            user.setPasswordEncoded(passwordEncoder.encode(user.getPasswordEncoded()));
            user.setRoles(List.of("DOCTOR"));
            user.setCodEntity(currentUser.getCodEntity());

            userService.save(user);
            URI location = fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();

            return ResponseEntity.created(location).body(user);
        } else {
            return new ResponseEntity<User>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/admin")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> registerEntity(@RequestBody UserDoctorDTO UserDoctorDTO) throws NotFoundException {
        User user = new User(UserDoctorDTO);

        if (!userService.existUsername(user.getUsername())) {
            String imageUrl = "/static/avatar/predAdminAvatar.png";
            try {
                Resource image = new ClassPathResource(imageUrl);
                user.setProfileAvatarFile(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            user.setPasswordEncoded(passwordEncoder.encode(user.getPasswordEncoded()));
            user.setRoles(List.of("ADMIN", "DOCTOR"));

            // Verificar si el codEntity está presente, si no, generar uno nuevo
            if (user.getCodEntity() == null || user.getCodEntity().equals("")) {
                Long generatedCodEntity = userService.generateUniqueCodEntity();
                user.setCodEntity(generatedCodEntity);
            }

            userService.save(user);
            URI location = fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();

            return ResponseEntity.created(location).body(user);
        } else {
            return new ResponseEntity<User>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
            @RequestParam(value = "address") String address,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "postalCode", required = false) String postalCode,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "profileAvatarFile", required = false) MultipartFile profileAvatarFile)
            throws IOException, SerialException, SQLException {

        User user = userService.findById(id).orElseThrow();
        if (address != null) {
            user.setAddress(address);
        }
        if (city != null) {
            user.setCity(city);
        }
        if (country != null) {
            user.setCountry(country);
        }
        if (postalCode != null) {
            user.setPostalCode(postalCode);
        }
        if (phone != null) {
            user.setPhone(phone);
        }
        if (profileAvatarFile != null) {
            byte[] imageBytes = profileAvatarFile.getBytes();
            Blob imageBlob = new SerialBlob(imageBytes);
            user.setProfileAvatarFile(imageBlob);
        }
        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/pass/{id}")
    public ResponseEntity<?> updateUserPassword(@PathVariable Long id,
            @RequestParam(value = "password") String password)
            throws IOException, SerialException, SQLException {

        User user = userService.findById(id).orElseThrow();
        if (password != null) {
            user.setPasswordEncoded(passwordEncoder.encode(password));
        }
        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/check-password/{id}")
    public ResponseEntity<Boolean> checkPassword(@PathVariable Long id,
            @RequestParam(value = "password") String currentPassword) {
        User currentUser = userService.findById(id).orElseThrow();
        boolean matches = passwordEncoder.matches(currentPassword, currentUser.getEncodedPassword());
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/userList")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> userList = userService.findAll();
        return ResponseEntity.ok(userList);
    }

    @GetMapping("/userList/doctor={id}")
    public ResponseEntity<List<User>> getAllUsersByDoctor(@PathVariable Long id) {
        List<User> userList = userService.findAllUsersByDoctorAsignatedId(id);
        return ResponseEntity.ok(userList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.findByEmail(email);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<User> getUserByName(@PathVariable String name) {
        Optional<User> user = userService.findByName(name);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/me/edit")
    public ResponseEntity<User> editMe(HttpServletRequest request, @RequestBody UserEditDTO userDTO) {

        Principal principal = request.getUserPrincipal();
        Optional<User> user = userService.findByEmail(principal.getName());
        User useraux = user.get();
        useraux.setEmail(userDTO.getEmail());
        useraux.setPasswordEncoded(userDTO.getPasswordEncoded());
        useraux.setAddress(userDTO.getAddress());
        useraux.setCity(userDTO.getCity());
        useraux.setCountry(userDTO.getCountry());
        useraux.setPostalCode(userDTO.getPostalCode());
        useraux.setPhone(userDTO.getPhone());
        userService.save(useraux);
        return ResponseEntity.ok(user.get());
    }

    @GetMapping("/profileAvatarFile/{id}")
    public ResponseEntity<Resource> downloadProfileAvatar(@PathVariable long id) throws SQLException {
        Optional<User> user = userService.findById(id);

        if (user.isPresent()) {
            Resource file = new InputStreamResource(user.get().getProfileAvatarFile().getBinaryStream());

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .contentLength(user.get().getProfileAvatarFile().length()).body(file);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUserData(@PathVariable("userId") Long userId) {
        Optional<User> user = userService.findById(userId);
        try {
            if (user.isPresent()) {
                List<Appointment> appointmentList = appointmentService.findByUserId(userId);
                appointmentService.deleteAll(appointmentList);

                userService.delete(userId);
            }
            return ResponseEntity.ok("UserData deleted for user with id: " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting user data: " + e.getMessage());
        }
    }

    @GetMapping("/rol/{id}")
    public boolean checkAdmin(@PathVariable long id) {
        Optional<User> user = userService.findById(id);
        int userRole = user.get().getRoles().size();
        return userRole > 1;
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<Resource> createProfileImage(@PathVariable long id, @RequestParam MultipartFile image,
            HttpServletRequest request) throws URISyntaxException, SQLException, IOException {
        Optional<User> user = userService.findById(id);
        if (user.isPresent() && image != null && !image.isEmpty()) {
            User newUser = user.get();
            try {
                newUser.setProfileAvatarFile(BlobProxy.generateProxy(image.getInputStream(), image.getSize()));
                userService.save(newUser);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request).replacePath(null).build()
                    .toUriString();
            Resource file = new InputStreamResource(image.getInputStream());

            URI location = new URI(baseUrl + "/api/users/" + id + "/image");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/jpeg", HttpHeaders.CONTENT_LOCATION, location.toString())
                    .contentLength(newUser.getProfileAvatarFile().length()).body(file);

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<User>> getUserByNameOrLastName(@PathVariable("name") String name) {
        List<User> users = userService.findByNameContaining(name);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/search")
    public List<User> searchUsersByNameOrLastName(@RequestParam String query) {
        return userService.findUsersByNameOrLastNameOrUsername(query, query, query);
    }

    @GetMapping("/doctorAsignated/{id}")
    public User getDoctorAsignated(@PathVariable Long id) {
        User patient = userService.findById(id).orElseThrow();
        return patient.getDoctorAsignated();
    }

    @GetMapping("/cod/{codEntity}")
    public ResponseEntity<List<User>> getUsersByCodEntity(@PathVariable("codEntity") Long codEntity) {
        List<User> users = userService.findByCodEntity(codEntity);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}

class PasswordCheckRequest {
    private String password;

    // Getters y setters
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}