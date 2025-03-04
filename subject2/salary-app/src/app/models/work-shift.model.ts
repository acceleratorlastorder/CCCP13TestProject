import { Docker } from "./docker.model";

export interface WorkShift {
    id?: number;
    docker?: Docker;
    type?: 'jour' | 'nuit' | 'week-end' | 'férié' | 'week-end-férié';
    startTime?: string;
    endTime?: string;
    weekend?: boolean;
    holiday?: boolean;
    validated?: boolean;
} 