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
        return await this.databaseService.getAll(Message)
            .catch(err => {
                Logger.warn('ConversationService', `Couldn't get conversations.`);
                throw err;
            });
    } 

    public async sendMessage(doc: any): Promise<{}> {
        return await this.databaseService.create(Message, doc)
            .catch(err => {
                Logger.warn('ConversationService', `Couldn't create conversation : ${doc}`);
                throw err;
            });
    }
}
