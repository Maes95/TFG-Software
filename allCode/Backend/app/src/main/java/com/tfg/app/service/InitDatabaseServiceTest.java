package com.tfg.app.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Blob;
import java.time.LocalDate;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.sql.rowset.serial.SerialBlob;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tfg.app.model.User;
import com.tfg.app.model.Util;

@Service
@Profile("test")
public class InitDatabaseServiceTest {
    @Autowired
    private UserService users;
    @Autowired
    private AppointmentService appointments;
    @Autowired
    private InterventionService interventions;
    @Autowired
    private DocumentService documents;
    @Autowired
    private UtilService utilService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DescriptionService descriptionService;

    @PostConstruct
    public void init() {

        createSuperAdmin(new User());
        createEntities();
        createDoctors();
        createUsers();
        // createAppointmentToUser();
        // createInterventionToAppointment();
        // createDescriptionsAndInterventionsType();

    }

    private void createUsers() {
        User user2 = new User();
        user2.setName("Sergio");
        user2.setLastName("Cuadros Flores");
        user2.setUsername("54362066W");
        user2.setPhone("444444444");
        user2.setEmail("sercua.flores@gmail.com");
        user2.setPasswordEncoded(passwordEncoder.encode("pass"));
        user2.setRoles(List.of("USER"));
        user2.setBirth(LocalDate.of(2002, 12, 24));
        user2.setGender("Masculino");
        user2.setAddress("Calle Benito Perez");
        user2.setCity("Madrid");
        user2.setCountry("España");
        user2.setPostalCode("28220");
        user2.setCodEntity(200L);
        String avatarUrlUser = "/static/avatar/predAvatar.png";
        try {
            setProfileAvatarContent(user2, avatarUrlUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
        User doctor = users.findById(4L).orElseThrow();
        user2.setDoctorAsignated(doctor);
        users.save(user2);

        Util util = new Util();
        util.setId(23L);
        util.setAppointmentsCompletedYesterday(0);
        util.setNumPatientsTotal(11);
        util.setNumPatientsYesterday(3);
        utilService.save(util);

    }

    public Blob downloadImage(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        try (InputStream inputStream = connection.getInputStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int n;
            while ((n = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, n);
            }
            Blob blob = new SerialBlob(outputStream.toByteArray());
            return blob;
        } finally {
            connection.disconnect();
        }
    }

    private void createEntities() {
        User entity1 = new User();
        entity1.setName("Fernando");
        entity1.setLastName("Ane");
        entity1.setUsername("33W");
        entity1.setPhone("711548969");
        entity1.setEmail("admin100@smilelink.es");
        entity1.setPasswordEncoded(passwordEncoder.encode("12345"));
        entity1.setRoles(List.of("DOCTOR", "ADMIN"));
        entity1.setBirth(LocalDate.now());
        entity1.setGender("Masculino");
        entity1.setCodEntity(100L);
        String avatarUrlAdmin = "/static/avatar/predAdminAvatar.png";
        try {
            setProfileAvatarContent(entity1, avatarUrlAdmin);
        } catch (IOException e) {
            e.printStackTrace();
        }
        users.save(entity1);

        Util util = new Util(0, 0, 0);
        utilService.save(util);

        User entity2 = new User();

        entity2.setName("Jose Luis");
        entity2.setLastName("Rodriguez");
        entity2.setUsername("332W");
        entity2.setPhone("785264122");
        entity2.setEmail("admin200@smilelink.es");
        entity2.setCodEntity(200L);
        entity2.setPasswordEncoded(passwordEncoder.encode("12345"));
        entity2.setRoles(List.of("DOCTOR", "ADMIN"));
        entity2.setBirth(LocalDate.now());
        entity2.setGender("Masculino");
        avatarUrlAdmin = "/static/avatar/predAdminAvatar.png";
        try {
            setProfileAvatarContent(entity2, avatarUrlAdmin);
        } catch (IOException e) {
            e.printStackTrace();
        }
        users.save(entity2);
    }

    private void createSuperAdmin(User userSuperAdmin) {
        userSuperAdmin.setName("Administrador");
        userSuperAdmin.setLastName("Administrador");
        userSuperAdmin.setUsername("");
        userSuperAdmin.setEmail("admin@smilelink.es");
        userSuperAdmin.setPasswordEncoded(passwordEncoder.encode("superpassword12345"));
        userSuperAdmin.setRoles(List.of("ADMIN"));
        String avatarUrlAdmin = "/static/avatar/administrador.png";
        try {
            setProfileAvatarContent(userSuperAdmin, avatarUrlAdmin);
        } catch (IOException e) {
            e.printStackTrace();
        }
        users.save(userSuperAdmin);
    }

    private void setProfileAvatarContent(User user, String profileAvatarUrl) throws IOException {
        Resource image = new ClassPathResource(profileAvatarUrl);
        user.setProfileAvatarFile(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));
    }

    public void createDoctors() {
        User user = new User();
        user.setName("Jaime");
        user.setLastName("Flores");
        user.setUsername("37W");
        user.setPhone("444444444");
        user.setEmail("jaime.flores@smilelink.es");
        user.setPasswordEncoded(passwordEncoder.encode("pass"));
        user.setRoles(List.of("DOCTOR"));
        user.setBirth(LocalDate.now().minusYears(20 + 11));
        user.setGender("Masculino");
        user.setCodEntity(200L);

        String avatarUrl = "/static/avatar/predAdminAvatar.png";

        try {
            setProfileAvatarContent(user, avatarUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        users.save(user);
    }
}