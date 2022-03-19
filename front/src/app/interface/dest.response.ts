import { Geometry } from "./geometry";

export interface DestResponse {
    geometry:Geometry;
    name: number;
    rating?: number;
    price_level?:number;
    user_ratings_total?:number;
    formatted_address?:string;
    errorMessage?:string;
}