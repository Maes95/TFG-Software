package com.tfg.app.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.sql.Blob;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.sql.rowset.serial.SerialBlob;

import org.hibernate.engine.jdbc.BlobProxy;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tfg.app.model.Appointment;
import com.tfg.app.model.Description;
import com.tfg.app.model.Document;
import com.tfg.app.model.Intervention;
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
        // createEntities();
        // createDoctors();
        // createUsers();
        // createAppointmentToUser();
        // createInterventionToAppointment();
        // createDescriptionsAndInterventionsType();

    }

    private void createInterventionToAppointment() {
        // ID appointment: 29
        Intervention intervention = new Intervention();
        Appointment appointment = appointments.findById(29L).orElseThrow();
        intervention.setAppointment(appointment);
        intervention.setInterventionDate(LocalDate.now());
        intervention.setUser(users.findById(22L).orElseThrow());
        intervention.setType(appointment.getDescription());
        Document document = new Document();
        byte[] bytes = convertToByte("Backend/app/src/main/resources/static/avatar/pdf-ejemplo.pdf");
        document.setFile(bytes);
        document.setFileName("pdf-ejemplo.pdf");
        document.setUser(users.findById(22L).orElseThrow());
        intervention.setDocument(document);
        interventions.save(intervention);
        document.setIntervention(interventions.findById(31L).orElseThrow());
        document.setCreationDate(LocalDate.now());
        documents.save(document);

        // ID appointment: 30 (Sin documento)
        Intervention intervention2 = new Intervention();
        Appointment appointment2 = appointments.findById(30L).orElseThrow();
        intervention2.setAppointment(appointment2);
        intervention2.setInterventionDate(LocalDate.now());
        intervention2.setUser(users.findById(22L).orElseThrow());
        intervention2.setType(appointment2.getDescription());
        interventions.save(intervention2);

    }

    private byte[] convertToByte(String filePath) {
        try {
            File file = new File("Backend/app/src/main/resources/static/avatar/pdf-ejemplo.pdf");

            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            for (int readNum; (readNum = fis.read(buf)) != -1;) {
                bos.write(buf, 0, readNum);
            }
            byte[] bytes = bos.toByteArray();
            fis.close();
            bos.close();
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createAppointmentToUser() {
        List<LocalDate> bookDateList = List.of(
                LocalDate.parse("2024-02-12"),
                LocalDate.parse("2023-12-23"),
                LocalDate.parse("2024-03-05"),
                LocalDate.parse("2023-07-25"),
                LocalDate.parse("2024-04-19"));
        List<LocalTime> fromTimeList = List.of(
                LocalTime.parse("09:30"),
                LocalTime.parse("11:45"),
                LocalTime.parse("13:00"),
                LocalTime.parse("14:00"),
                LocalTime.parse("15:33"));

        List<String> descriptionList = List.of("Mantenimiento y Prevención",
                "Problemas Comunes y Tratamientos de Rutina", "Ortodoncia y Estética Dental",
                "Procedimientos Quirúrgicos y Restauradores", "Problemas Específicos y Emergencias");

        User patient = users.findById(22L).orElseThrow();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            Appointment appointment = new Appointment();
            appointment.setUser(patient);
            appointment.setBookDate(bookDateList.get(random.nextInt(bookDateList.size())));
            LocalTime fromDate = fromTimeList.get(random.nextInt(fromTimeList.size()));
            appointment.setFromDate(fromDate);
            appointment.setToDate(fromDate.plusMinutes(20));
            appointment.setCompleted(false);
            appointment.setCodEntity(200L);
            appointment.setDoctorAsignated(users.findById(11L).orElseThrow());
            appointment.setDescription(descriptionList.get(random.nextInt(descriptionList.size())));
            appointments.save(appointment);
        }
        Appointment appointment = new Appointment();
        appointment.setUser(patient);
        appointment.setBookDate(bookDateList.get(random.nextInt(bookDateList.size())));
        LocalTime fromDate = fromTimeList.get(random.nextInt(fromTimeList.size()));
        appointment.setFromDate(fromDate);
        appointment.setToDate(fromDate.plusMinutes(20));
        appointment.setCompleted(true);
        appointment.setCodEntity(200L);
        appointment.setDoctorAsignated(users.findById(11L).orElseThrow());
        appointment.setDescription(descriptionList.get(random.nextInt(descriptionList.size())));
        appointments.save(appointment);

        Appointment appointment2 = new Appointment();
        appointment2.setUser(patient);
        appointment2.setBookDate(bookDateList.get(random.nextInt(bookDateList.size())));
        LocalTime fromDate2 = fromTimeList.get(random.nextInt(fromTimeList.size()));
        appointment2.setFromDate(fromDate2);
        appointment2.setToDate(fromDate2.plusMinutes(20));
        appointment2.setCompleted(true);
        appointment2.setCodEntity(200L);
        appointment2.setDoctorAsignated(users.findById(11L).orElseThrow());
        appointment2.setDescription(descriptionList.get(random.nextInt(descriptionList.size())));
        appointments.save(appointment2);

    }

    private void createUsers() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        "https://randomuser.me/api/?results=10&inc=gender,name,location,email,dob,phone,cell,id,picture,nat"))
                .header("accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            JSONObject obj = new JSONObject(response.body());

            JSONArray array = obj.getJSONArray("results");

            for (int i = 0; i < array.length(); i++) {
                JSONObject person = array.getJSONObject(i);
                JSONObject nameObject = person.getJSONObject("name");
                JSONObject locationObject = person.getJSONObject("location");
                JSONObject dobObject = person.getJSONObject("dob");

                User user = new User();
                user.setName(nameObject.getString("first"));
                user.setLastName(nameObject.getString("last"));
                user.setUsername(person.getJSONObject("id").optString("value", person.getString("phone")));
                LocalDate birthDate = LocalDate.parse(dobObject.getString("date").substring(0, 10));
                user.setBirth(birthDate);
                user.setCodEntity(i % 2 == 0 ? 100L : 200L);
                user.setPhone(person.getString("phone"));
                user.setEmail(person.getString("email"));
                user.setCity(locationObject.getString("city"));
                user.setAddress(locationObject.getJSONObject("street").getInt("number") + " "
                        + locationObject.getJSONObject("street").getString("name"));
                String postalCode = String.valueOf(locationObject.optInt("postcode", 28820));
                user.setPostalCode(postalCode);
                user.setGender("male".equals(person.getString("gender")) ? "Masculino" : "Femenino");
                user.setCountry(locationObject.getString("country"));
                user.setProfileAvatarFile(downloadImage(person.getJSONObject("picture").getString("large")));
                user.setPasswordEncoded(passwordEncoder
                        .encode(person.getJSONObject("id").optString("value", person.getString("phone"))));
                user.setRoles(List.of("USER"));
                List<Long> lista1 = List.of(2L, 5L, 7L, 9L);
                List<Long> lista2 = List.of(4L, 6L, 8L, 10L, 11L);
                Random random = new Random();
                Long doctorId = i % 2 == 0 ? lista1.get(random.nextInt(lista1.size()))
                        : lista2.get(random.nextInt(lista2.size()));
                User doctor = users.findById(doctorId).orElseThrow();
                user.setDoctorAsignated(doctor);
                users.save(user);

            }

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
            User doctor = users.findById(11L).orElseThrow();
            user2.setDoctorAsignated(doctor);
            users.save(user2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Util util = new Util(0, 3, 11);
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
        entity2.setRoles(List.of( "DOCTOR", "ADMIN"));
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

    private LocalTime convertDurationToLocalTime(String duration) {
        try {
            int totalMinutes = Integer.parseInt(duration.replaceAll("[^0-9]", ""));
            int hours = totalMinutes / 60;
            int minutes = totalMinutes % 60;
            return LocalTime.of(hours, minutes);
        } catch (NumberFormatException e) {
            return LocalTime.of(0, 0);
        }
    }

    private void createDescriptionsAndInterventionsType() {
        List<String> descriptionList = List.of("Mantenimiento y Prevención",
                "Problemas Comunes y Tratamientos de Rutina", "Ortodoncia y Estética Dental",
                "Procedimientos Quirúrgicos y Restauradores", "Problemas Específicos y Emergencias");

        List<Map<String, String>> interventionTypeList = List.of(
                Map.of(
                        "Chequeos y limpiezas regulares", "30",
                        "Consulta sobre hábitos orales", "15",
                        "Prevención de enfermedades dentales y educación sobre higiene oral", "15"),
                Map.of(
                        "Dolor de muelas", "5",
                        "Dolor de encías", "90",
                        "Obturación simple", "20",
                        "Obturacion compuesta", "30",
                        "Gran reconstrucción", "60",
                        "Problemas de encías", "45"),
                Map.of(
                        "Tratamientos de ortodoncia", "30",
                        "Blanqueamiento dental", "45",
                        "Mejoras estéticas", "90"),
                Map.of(
                        "Extracciones dentales", "60",
                        "Implantes y prótesis dentales", "60"),
                Map.of(
                        "Emergencias dentales", "45",
                        "Trastorno de la articulacion temporomandibular", "45",
                        "Revisión muelas de juicio", "15",
                        "Problemas de mordida o habla", "15"));

        for (int i = 0; i < descriptionList.size(); i++) {
            for (Map.Entry<String, String> entry : interventionTypeList.get(i).entrySet()) {
                Description description = new Description();
                description.setNameIntervention(entry.getKey());
                description.setNameDescription(descriptionList.get(i));
                LocalTime duration = convertDurationToLocalTime(entry.getValue());
                description.setTimeToIntervention(duration);
                descriptionService.save(description);
            }
        }
    }

    private void setProfileAvatarContent(User user, String profileAvatarUrl) throws IOException {
        Resource image = new ClassPathResource(profileAvatarUrl);
        user.setProfileAvatarFile(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));
    }

    public void createDoctors() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        "https://randomuser.me/api/?results=6&inc=gender,name,location,email,dob,phone,cell,id,picture,nat"))
                .header("accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            JSONObject obj = new JSONObject(response.body());

            JSONArray array = obj.getJSONArray("results");

            for (int i = 0; i < array.length(); i++) {
                JSONObject person = array.getJSONObject(i);
                JSONObject nameObject = person.getJSONObject("name");
                JSONObject dobObject = person.getJSONObject("dob");

                User user = new User();
                user.setName(nameObject.getString("first"));
                user.setLastName(nameObject.getString("last"));
                user.setUsername(person.getJSONObject("id").optString("value", person.getString("phone")));
                LocalDate birthDate = LocalDate.parse(dobObject.getString("date").substring(0, 10));
                user.setBirth(birthDate);
                user.setCodEntity(i % 2 == 0 ? 100L : 200L);
                user.setPhone(person.getString("phone"));
                user.setEmail(nameObject.getString("first") + "." + nameObject.getString("last") + "@smilelink.es");
                user.setGender("male".equals(person.getString("gender")) ? "Masculino" : "Femenino");
                user.setProfileAvatarFile(downloadImage(person.getJSONObject("picture").getString("large")));
                user.setPasswordEncoded(passwordEncoder
                        .encode(person.getJSONObject("id").optString("value", person.getString("phone"))));
                user.setRoles(List.of("DOCTOR"));
                users.save(user);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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