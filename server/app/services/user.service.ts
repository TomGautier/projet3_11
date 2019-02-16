import { injectable, inject } from "inversify";
import { TYPES } from "../types";
import { DatabaseService } from "./database.service";
import User from "../schemas/user";

require('reflect-metadata');

@injectable()
export class UserService {
    private readonly USERNAME_CRITERIA = "username";

    constructor(@inject(TYPES.DatabaseService) private databaseService: DatabaseService) {
    }

    public async find(username: string): Promise<{}> {
        return await this.databaseService.getByCriteria(User, this.USERNAME_CRITERIA, username);
    }

    public async create(user: any): Promise<{}> {
        return await this.databaseService.create(User, user);
    }

    public async removeByUsername(username: string): Promise<{}> {
        return await this.databaseService.remove(User, this.USERNAME_CRITERIA, username);
    }
}