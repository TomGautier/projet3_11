import { injectable, inject } from "inversify";
import { TYPES } from "../types";
import { DatabaseService } from "./database.service";
import Message from "../schemas/message";
import { MessageServiceInterface } from "../interfaces";
import { Logger } from "./logger.service";

require('reflect-metadata');

require('reflect-metadata');

@injectable()
export class MessageService implements MessageServiceInterface {
    private readonly ID_CRITERIA = "id";
    
    constructor(@inject(TYPES.DatabaseService) private databaseService: DatabaseService) {
    }

    public async getAllFromConversation(conversation: string): Promise<{}> {
        try {
            return await this.databaseService.getAll(Message);
        }
        catch (err) {
            Logger.warn('ConversationService', `Couldn't get conversations.`);
            return {
                title: "Error",
                body: err.toString()
            };
        }
    } 

    public async sendMessage(doc: any): Promise<{}> {
        try {

            return await this.databaseService.create(Message, doc);
        }
        catch (err) {
            Logger.warn('ConversationService', `Couldn't create conversation : ${doc}`);
            return {
                title: "Error",
                body: err.toString()
            };
        }
    }
}
