import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from 'src/app/models/user.model';
import { AuthService } from 'src/app/services/auth.service';
import { EmailService } from 'src/app/services/email.service';
import { UserService } from 'src/app/services/user.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-edit-patient',
  templateUrl: './edit-patient.component.html'
})

export class EditPatientComponent {

  userId!: number;
  user!: User;
  profileAvatarUrls!: string
  address!: string
  city!: string
  country!: string
  postalCode!: string
  phone!: string
  avatarFile!: File;

  constructor(private emailService: EmailService, private router: Router, private userService: UserService, public authService: AuthService, private activatedRoute: ActivatedRoute) { }

  ngOnInit(): void {
    this.userId = this.activatedRoute.snapshot.params['id'];
    this.userService.getUser(this.userId).subscribe((response) => {
      this.user = response;


      this.userService.getProfileAvatar(response.id).subscribe(blob => {
        const objectUrl = URL.createObjectURL(blob);
        this.profileAvatarUrls = objectUrl;
      });
    });
  }

  onFileSelected(event: any) {
    if (event.target.files && event.target.files.length > 0) {
      this.avatarFile = event.target.files[0];
    }

  }

  resetPassword() {
    this.emailService.sendEmailRecovery(this.user.email, this.user.username);
    this.editPassword();
    Swal.fire("Se ha enviado un correo electrónico con sus nuevas credenciales", "", "info");
  }
  
  editPassword() {
    if (this.user) {
      this.user.encodedPassword = this.user.username;
      console.log(this.user.encodedPassword)
      this.userService.updatePassword(this.user).subscribe(
        (_) => {
          console.log(this.user);
          window.history.back();
        }
      )
    }
  }

  editUser() {
    Swal.fire({
      title: "¿Estas seguro/a que quieres confirmar los cambios?",
      showDenyButton: true,
      showCancelButton: false,
      confirmButtonText: "Actualizar",
      denyButtonText: `Cancelar`
    }).then((result) => {
      /* Read more about isConfirmed, isDenied below */
      if (result.isConfirmed) {
        if (this.user) {
          if (this.address)
            this.user.address = this.address;
          if (this.city)
            this.user.city = this.city;
          if (this.country)
            this.user.country = this.country;
          if (this.postalCode)
            this.user.postalCode = this.postalCode;
          if (this.phone)
            this.user.phone = this.phone;
          this.userService.updateUser(this.user, this.avatarFile).subscribe(
            (_) => {
              console.log(this.user);
              this.ngOnInit();
              window.history.back();
            },
          );
          Swal.fire("Actualizado", "", "success");
        }
      } else if (result.isDenied) {
        Swal.fire("Cancelado", "", "info");
        window.history.back();
      }
    });
  }

}

