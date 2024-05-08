import { HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from 'src/app/models/user.model';
import { AppointmentService } from 'src/app/services/appointment.service';
import { InterventionService } from 'src/app/services/intervention.service';
import { UserService } from 'src/app/services/user.service';
import { UtilService } from 'src/app/services/util.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-add-intervention',
  templateUrl: './add-intervention.component.html'
})
export class AddInterventionComponent implements OnInit {


  selectedFile: File | null = null;
  selectedFileName: string = '';
  descriptionAppointment!: string;
  appointmentId!: number;
  userId!: number;
  user!: User;
  type!: string;
  dni!: string;

  constructor(private userService: UserService, private appointmentService: AppointmentService, private interventionService: InterventionService, private activatedRoute: ActivatedRoute, private utilService: UtilService) { }

  ngOnInit(): void {
    this.appointmentId = this.activatedRoute.snapshot.params['idAppointment'];
    console.log("APO: ", this.appointmentId);
    this.userId = this.activatedRoute.snapshot.params['idUser'];
    console.log("USE:", this.userId);
    this.userService.getUser(this.userId).subscribe(user => {
      this.user = user;
      this.dni = user.username;
    })

    this.appointmentService.getAppointment(this.appointmentId).subscribe(response =>
      this.descriptionAppointment = response.description)



  }

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
    this.selectedFileName = this.selectedFile?.name || '';
  }

  generarInforme(event: Event) {
    event.preventDefault();
    event.stopPropagation();
    const headers = new HttpHeaders({ 'Content-Type': 'application/json', 'Accept': 'application/pdf' });

    const data = {
      name: this.user.name, // Asegúrate de que estás recogiendo estos valores correctamente
      lastName: this.user.lastName,
      dni: this.dni,
      phone: this.user.phone,
      address: this.user.address + ", " + this.user.city + ", " + this.user.postalCode + ", " + this.user.country,
      descriptionAppointment: this.descriptionAppointment,
      reportIntervention: this.type
    };

    this.utilService.generatePDF(data).subscribe(response => {
      const blob = new Blob([response], { type: 'application/pdf' });
      const downloadURL = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = downloadURL;
      link.download = data.dni + '_' + Date.now() + '.pdf';
      link.click();
    }, (error: any) => {
      console.error('Error al generar el informe:', error);
    });

  }

  submit() {
    const formData = new FormData();
    if (this.type != null && this.selectedFile) {
      console.log(this.type);
      formData.append('file', this.selectedFile);
      formData.append('type', this.type);
    } else if (this.type && !this.selectedFile) {
      formData.append('file', "");
      formData.append('type', this.type);
    } else {
      console.error("Ningun archivo seleccionado")
    }
    this.interventionService.addIntervention(this.appointmentId, this.userId, formData).subscribe(
      (_) => {
        Swal.fire("Intervención añadida", "", "success");
        this.back();
      },
      (error) => {
        Swal.fire("Ha ocurrido un error", "", "error");
        console.error(error);
        this.back();
      }
    );
  }

  back() {
    window.history.back();
  }
}
