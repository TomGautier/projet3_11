import { inject } from "inversify";
import { TYPES } from "../types";
import { ConversationManager } from "../services/conversation.manager";

export class ConversationController {
    constructor(
        @inject(TYPES.ConversationManager) private conversationManager: ConversationManager
    ) { }
}
