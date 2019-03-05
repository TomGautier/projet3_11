import { injectable, inject } from "inversify";
import { TYPES } from "../types";
import { DatabaseService } from "./database.service";
import Conversation from "../schemas/conversation";
import { ConversationServiceInterface } from "../interfaces";
import { Logger } from "./logger.service";

require('reflect-metadata');

@injectable()
export class ConversationService implements ConversationServiceInterface {
    private readonly NAME_CRITERIA = "name";
    private readonly PARTICIPANTS_CRITERIA = "participants";

    constructor(@inject(TYPES.DatabaseService) private databaseService: DatabaseService) {
    }

    public async getByName(name: string): Promise<{}> {
        return await this.databaseService.getByCriteria(Conversation, this.NAME_CRITERIA, name)
            .catch(err => {
                Logger.warn('ConversationService', `This conversation doesn't exist : ${name}`);
                throw err;
            });
    }

    public async getAllByUsername(username: string): Promise<{}> {
        return await this.databaseService.getAllByCriteria(Conversation, this.PARTICIPANTS_CRITERIA, username)
            .catch(err => {
                Logger.warn('ConversationService', `Couldn't get conversations from ${username}.`);
                throw err;
            });
    } 

    public async create(conversationName: string, creatorUsername: string): Promise<{}> {
        const conversation =  new Conversation({
            name: conversationName,
            participants: [creatorUsername]
        });
            
        return await this.databaseService.create(Conversation, conversation)
            .catch(err => {
                Logger.warn('ConversationService', `Couldn't create conversation : ${conversationName}`);
                throw err;
           });
    }
}