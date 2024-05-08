import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from 'src/app/models/user.model';
import { AuthService } from 'src/app/services/auth.service';
import { EmailService } from 'src/app/services/email.service';
import { UserService } from 'src/app/services/user.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-add-patient',
  templateUrl: './add-patient.component.html',
  styleUrls: ['./add-patient.component.scss']
})
export class AddPatientComponent implements OnInit {
  name: string = '';
  lastName: string = '';
  email: string = '';
  username: string = '';
  passwordEncoded: string = 'pass';
  gender: string = '';
  phone: string = '';
  birth: string = '';
  address: string = '';
  country: string = '';
  city: string = '';
  postalCode: string = '';
  doctorAsignated!: User;
  codEntity!: number;


  constructor(private userService: UserService, public authService: AuthService, private router: Router, private emailService: EmailService) { }

  ngOnInit(): void {
    this.userService.getMe().subscribe(response => {
      this.doctorAsignated = response;
      this.codEntity = response.codEntity;
      console.log(this.codEntity)
    })
  }

  addPatient() {
    if (this.authService.isAdmin() || this.authService.isDoctor()) {
      const userData = {
        name: this.name,
        lastName: this.lastName,
        username: this.username,
        email: this.email,
        passwordEncoded: this.username,
        address: this.address,
        city: this.city,
        country: this.country,
        postalCode: this.postalCode,
        phone: this.phone,
        gender: this.gender,
        birth: this.birth,
        doctorAsignated: this.doctorAsignated,
        codEntity: this.codEntity
      }
      if (Object.values(userData).every(value => value !== '')) {
        this.authService.register(userData).subscribe(
          (_) => {
            Swal.fire("Usuario registrado", "", "success");
            try {
              this.emailService.sendEmail(this.email, this.username);
            } catch (error) {
              console.log(error)
              Swal.fire("NO SE HAN ENVIADO LAS CREDENCIALES", "", "error");

            }
            window.history.back();
          },
          (_) => {
            Swal.fire("Probablemente este usuario ya exista, sino vuelva a intentarlo", "", "error");
            console.log(_)
            console.error("Error detail:", _.error);
            console.error("error");
            this.router.navigate(['/error-page'])
          }
        );
      } else {
        Swal.fire('Todos los campos son obligatorios', '', 'warning');
      }
    } else {
      console.error(this.authService.currentUser())
    }
  }
}
