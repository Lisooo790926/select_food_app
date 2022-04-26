import { AdditionItem } from "./additonal.item";
import { DestResponse } from "./dest.response";
import { FindingHistory } from "./finding.history";

export interface CustomResponse {
    status: string;
    message: string;
    data: {
        destResponse?: DestResponse,
        histories?: FindingHistory[],
        additionalItems?: AdditionItem[],
        saveItem?: AdditionItem,
        errorMessage?: string, 
        apitoken? :string
    };
}