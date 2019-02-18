import { injectable, inject } from "inversify";
import { TYPES } from "../types";
import { DatabaseService } from "./database.service";
import Message from "../schemas/message";
import { MessageServiceInterface } from "../interfaces";
import { Logger } from "./logger.service";

require('reflect-metadata');

@injectable()
export class MessageService implements MessageServiceInterface {
    private readonly CONV_ID_CRITERIA = "conversationId";
    
    constructor(@inject(TYPES.DatabaseService) private databaseService: DatabaseService) {
    }

    public async getAllFromConversation(conversationId: string): Promise<{}> {
        return await this.databaseService.getAllByCriteria(Message, this.CONV_ID_CRITERIA, conversationId)
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
