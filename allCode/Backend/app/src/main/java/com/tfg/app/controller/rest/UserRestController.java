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

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
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
import com.tfg.app.controller.DTOS.UserEditDTO;
import com.tfg.app.model.Appointment;
import com.tfg.app.model.User;
import com.tfg.app.repository.UserRepository;
import com.tfg.app.service.AppointmentService;
import com.tfg.app.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    public ResponseEntity<User> register(@RequestBody UserDTO userDTO) {
        User user = new User(userDTO);

        if (!userService.existUsername(user.getUsername())) {
            String imageUrl = "/static/assets/predAvatar.png";
			try {
				Resource image = new ClassPathResource(imageUrl);
				user.setProfileAvatarFile(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));
			} catch (IOException e) {
				e.printStackTrace();
			}
            user.setPasswordEncoded(passwordEncoder.encode(user.getPasswordEncoded()));
            user.setRoles(Arrays.asList("USER"));

            userService.save(user);
            URI location = fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();

            return ResponseEntity.created(location).body(user);
        } else {
            return new ResponseEntity<User>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/{id}")
	public ResponseEntity<?> updateUser(@PathVariable long id,
			@RequestParam(value = "name", required= false) String name,
			@RequestParam(value = "lastName", required = false) String lastName,
			@RequestParam(value = "profileAvatarFile", required = false) MultipartFile profileAvatarFile) {
		try {
			User user = userService.findById(id).orElseThrow();
            if (name != null)
			    user.setName(name);
            if (lastName != null)
			    user.setLastName(lastName);

			if (profileAvatarFile != null) {
				byte[] imageBytes = profileAvatarFile.getBytes();
				Blob imageBlob = new SerialBlob(imageBytes);
				user.setProfileAvatarFile(imageBlob);
			}
			User updatedUser = userRepository.save(user);
			return ResponseEntity.ok(updatedUser);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating profile");
		}
	}

    @GetMapping("/userList")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> userList = userService.findAll();
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

    @PostMapping("/me/edit")
    public ResponseEntity<User> editMe(HttpServletRequest request, @RequestBody UserEditDTO userDTO) {

        Principal principal = request.getUserPrincipal();
        Optional<User> user = userService.findByEmail(principal.getName());
        User useraux = user.get();
        useraux.setEmail(userDTO.getEmail());
        useraux.setName(userDTO.getName());
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
}
