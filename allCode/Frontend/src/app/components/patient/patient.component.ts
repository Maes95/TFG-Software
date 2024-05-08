import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/models/user.model';
import { UserService } from 'src/app/services/user.service';
import { Router } from '@angular/router';
import { format } from 'date-fns';
import { UtilService } from 'src/app/services/util.service';
import { FormControl } from '@angular/forms';
import { debounceTime, forkJoin, of, switchMap } from 'rxjs';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-patient',
  templateUrl: './patient.component.html'
})
export class PatientComponent implements OnInit {

  loading: boolean = false;
  patientsList: User[] = [];
  profileAvatarUrls: string[] = [];
  page!: number;
  doctorAsignated!: number;
  showingAllUsers!: boolean;
  codEntity!: number;

  control = new FormControl();
  noResults: boolean = false


  constructor(private patientService: UserService, private router: Router, private utilService: UtilService) { }

  ngOnInit(): void {
    this.showingAllUsers = false;

    this.patientService.getMe().subscribe((response) => {
      this.doctorAsignated = response.id;
      this.codEntity = response.codEntity;
      this.getAllUsersByDoctor(this.doctorAsignated, this.codEntity);
    });

    this.observerChangeSearch();
  }


  getAllUsers(): void {
    this.loading = true;
    this.patientService.getUsersByCodEntity(this.codEntity).subscribe((list) => {
      this.patientsList = list.filter(patient =>
        patient.roles.length === 1 && patient.roles.includes('USER'));
      // Usar forkJoin para esperar todas las llamadas a getProfileAvatar
      forkJoin(
        this.patientsList.map(patient =>
          this.patientService.getProfileAvatar(patient.id).pipe(
            switchMap(blob => {
              const objectUrl = URL.createObjectURL(blob);
              this.profileAvatarUrls[patient.id] = objectUrl;
              return of(patient); // Devuelve el usuario después de manejar el avatar
            })
          )
        )
      ).subscribe(
        () => {
          this.loading = false;
        },
        (error) => {
          console.error('Error:', error);
          this.loading = false;
        }
      );
    });
  }

  getAllUsersByDoctor(doctorId: number, codEntity: number): void {
    this.loading = true;
    console.log(codEntity)
    this.patientService.getUserListByDoctor(doctorId).subscribe((list) => {
      this.patientsList = list.filter(patient =>
        patient.roles.length === 1 && patient.roles.includes('USER') && patient.codEntity === codEntity);
      // Usar forkJoin para esperar todas las llamadas a getProfileAvatar
      forkJoin(
        this.patientsList.map(patient =>
          this.patientService.getProfileAvatar(patient.id).pipe(
            switchMap(blob => {
              const objectUrl = URL.createObjectURL(blob);
              this.profileAvatarUrls[patient.id] = objectUrl;
              return of(patient); // Devuelve el usuario después de manejar el avatar
            })
          )
        )
      ).subscribe(
        () => {
          this.loading = false;
        },
        (error) => {
          console.error('Error:', error);
          this.loading = false;
        }
      );
    });
  }

  togglePatientView(): void {
    if (this.showingAllUsers) {
      this.getAllUsersByDoctor(this.doctorAsignated, this.codEntity);
    } else {
      this.getAllUsers();
    }
    this.showingAllUsers = !this.showingAllUsers;
  }

  goToProfile(id: number): any {
    this.router.navigate(['/profile/' + id])
  }

  deleteUser(user: User) {
    this.patientService.deleteUser(user);
    console.log("Usuario eliminado")
    this.ngOnInit();
  }

  exportPDF() {
    this.utilService.exportPatientsPDF(this.doctorAsignated).subscribe((data) => {
      const blob = new Blob([data], { type: 'application/pdf' });
      const link = document.createElement("a");
      link.href = window.URL.createObjectURL(blob);
      link.download = "Pacientes_" + format(Date.now(), "yyyy-MM-dd") + ".pdf";
      link.click();
    });
  }

  exportExcel() {
    this.utilService.exportPatientsExcel(this.doctorAsignated).subscribe((data) => {
      const blob = new Blob([data], { type: 'application/octet-stream' });
      const link = document.createElement("a");
      link.href = window.URL.createObjectURL(blob);
      link.download = "Pacientes_" + format(Date.now(), "yyyy-MM-dd") + ".xlsx";
      link.click();
    });
  }

  reload() {
    window.location.reload();
  }

  observerChangeSearch() {
    this.control.valueChanges
      .pipe(
        // startWith(''),
        debounceTime(500),
        switchMap(query => {
          this.loading = true;
          if (query.trim().length === 0) {
            this.noResults = false;
            return this.patientService.getUserList();
          } else {
            this.noResults = false;
            return this.patientService.getUsersByNameOrLastNameOrUsername(query);
          }
        })
      )
      .subscribe(result => {
        if (result.length === 0) {
          this.noResults = true;
        }
        this.patientsList = result;
        this.loading = false;
      });
  }

  showModalDelete(user: User) {
    Swal.fire({
      title: "Vas a eliminar el paciente",
      showDenyButton: true,
      showCancelButton: false,
      confirmButtonText: "Eliminar",
      denyButtonText: `Cancelar`
    }).then((result) => {
      if (result.isConfirmed) {
        this.deleteUser(user);
        Swal.fire("Eliminado", "", "success");
        this.ngOnInit();
        this.ngOnInit();
      } else if (result.isDenied) {
      }
    });
  }


}
