import { injectable, inject } from "inversify";
import { TYPES } from "../types";
import { DatabaseService } from "./database.service";
import User from "../schemas/user";
import { Logger } from "./logger.service";
import { ConnectionManager } from "./connection.service";
import { IUser } from "../interfaces/user";
import { IUserModel } from "../models/user";

require('reflect-metadata');

class UserStatus {
    constructor(
        public username: string,
        public connected: boolean
    ) { }
}

@injectable()
export class UserService {
    private readonly USERNAME_CRITERIA = "username";

    constructor(@inject(TYPES.DatabaseService) private databaseService: DatabaseService,
                @inject(TYPES.ConnectionManager) private connectionManager: ConnectionManager) { }

    public async getByUsername(username: string): Promise<{}> {
        return await this.databaseService.getByCriteria(User, this.USERNAME_CRITERIA, username)
            .catch(err => {throw err;});
    }

    public async getAll(): Promise<UserStatus[]> {
        const userStatuses: UserStatus[] = [];
        const users = await this.databaseService.getAll(User)
            .catch(err => {throw err;}) as IUser[];

        users.forEach((user) => {
            if (user.username != undefined) {
                userStatuses.push(new UserStatus(user.username, this.connectionManager.isConnected(user.username)));
            }
        });

        return userStatuses;
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