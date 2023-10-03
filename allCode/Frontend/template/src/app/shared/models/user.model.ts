import { Appointment } from "./appointment.model";
import { Intervention } from "./intervention.model";

export interface User{
    id: number;
    name: string;
    lastName: string;
    username: string;
    email: string;
    passwordEncoded: string;
    address: string;
    city: string;
    country: string;
    postalCode: number;
    phone: number;
    gender: string;
    birth: Date;
    roles: string[];
    profileAvatarFile: Blob;
    interventions: Intervention[],
    appointments: Appointment[];
  }