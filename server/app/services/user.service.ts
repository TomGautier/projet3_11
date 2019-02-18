import { injectable, inject } from "inversify";
import { TYPES } from "../types";
import { DatabaseService } from "./database.service";
import User from "../schemas/user";
import { Logger } from "./logger.service";
import { SocketService } from "./socket.service";

require('reflect-metadata');

@injectable()
export class UserService {
    private readonly USERNAME_CRITERIA = "username";

    constructor(@inject(TYPES.DatabaseService) private databaseService: DatabaseService) { }

    public async find(username: string): Promise<{}> {
        return await this.databaseService.getByCriteria(User, this.USERNAME_CRITERIA, username)
            .catch(err => {throw err;});
    }

    public async create(user: any): Promise<{}> {
        return await this.databaseService.create(User, user)
            .catch(err => {throw err;});
    }
    
    public async removeByUsername(username: string): Promise<{}> {
        return await this.databaseService.remove(User, this.USERNAME_CRITERIA, username)
            .catch(err => {throw err;});
    }
}