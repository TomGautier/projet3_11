import { injectable, inject } from "inversify";
import { TYPES } from "../types";
import { DatabaseService } from "./database.service";
import Conversation from "../schemas/conversation";

require('reflect-metadata');

@injectable()
export class UserService {
    private readonly ID_CRITERIA = "id";

    constructor(@inject(TYPES.DatabaseService) private databaseService: DatabaseService) {
    }

    public async find(id: string): Promise<{}> {
        return await this.databaseService.getByCriteria(Conversation, this.ID_CRITERIA, id);
    }

    public async create(message: any): Promise<{}> {
        return await this.databaseService.create(Conversation, message);
    }
}