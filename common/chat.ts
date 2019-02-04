import { User } from "./user";

export class Chat {

    constructor(
        public id: string,
        public creatorName?: string,
        public creatorId?: string,
        public users?: User[]
    ) { }
}