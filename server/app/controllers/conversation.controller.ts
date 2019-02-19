import { injectable, inject } from "inversify";
import { Router, Request, Response, NextFunction } from "express";

import { TYPES } from "../types";
import { ConversationService } from "../services/conversation.service";
import { ConversationControllerInterface } from "../interfaces";
import { ConnectionManager } from "../services/connection.service";

@injectable()
export class ConversationController implements ConversationControllerInterface {
    
    public constructor(
        @inject(TYPES.ConversationServiceInterface) private conversationService: ConversationService,
        @inject(TYPES.ConversationServiceInterface) private connectionManager: ConnectionManager
    ) { }

    public get router(): Router {
        const router: Router = Router();
        
        router.get("/:sessionId/:username",
            (req: Request, res: Response, next: NextFunction) => {
                // Send the request to the service and send the response
                if(!this.connectionManager.verifySession(req.params.sessionId, req.params.username)) 
                    { res.json("User is not authenticated"); return; }
                this.conversationService.getAllByUsername(req.params.username).then(conversations => {
                    res.json(conversations);
                });
            });

        router.post("/:sessionId/:username/:conversationName",
            (req: Request, res: Response, next: NextFunction) => {
                if(!this.connectionManager.verifySession(req.params.sessionId, req.params.username))
                    { res.json("User is not authenticated"); return; }
                this.conversationService.create(req.params.conversationName, req.params.username).then(conversation => {
                    res.json(conversation);
                });
            });
            
        return router;
    }
}