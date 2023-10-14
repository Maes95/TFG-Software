import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from 'src/app/models/user.model';
import { AuthService } from 'src/app/services/auth.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.scss']
})
export class EditProfileComponent implements OnInit {

  user!: User;
  profileAvatarUrls!: string
  address!: string
  city!: string
  country!: string
  postalCode!: string
  phone!: string
  avatarFile!: File;

  constructor(private router: Router, private userService: UserService, public authService: AuthService) { }

  ngOnInit(): void {
    this.userService.getMe().subscribe((response) => {
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

  editUser() {
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
          this.router.navigate(['/profile']);
        },
      );
    }
  }

}
