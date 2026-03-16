import { Materials } from "./materials";

export interface SiteData {
    name: string;
    surface: number;
    parking: number;
    energyConsum: number;
    employees: number;
    materials: Materials;
}

