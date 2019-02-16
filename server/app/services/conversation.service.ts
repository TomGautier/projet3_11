import { injectable, inject } from "inversify";
import { TYPES } from "../types";
import { DatabaseService } from "./database.service";
import Conversation from "../schemas/conversation";

require('reflect-metadata');

@injectable()
export class UserService {
    private readonly NAME_CRITERIA = "name";

    constructor(@inject(TYPES.DatabaseService) private databaseService: DatabaseService) {
    }

    public async find(name: string): Promise<{}> {
        return await this.databaseService.getByCriteria(Conversation, this.NAME_CRITERIA, name);
    }

    public async create(conversation: any): Promise<{}> {
        return await this.databaseService.create(Conversation, conversation);
    }
}