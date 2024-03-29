import { injectable, inject } from "inversify";
import { Router, Request, Response, NextFunction } from "express";

import { TYPES } from "../types";
import { ConversationService } from "../services/conversation.service";
import { ConversationControllerInterface } from "../interfaces";
import { UserManager } from "../services/user.manager";

@injectable()
export class ConversationController implements ConversationControllerInterface {
    
    public constructor(
        @inject(TYPES.ConversationServiceInterface) private conversationService: ConversationService,
        @inject(TYPES.UserManager) private userManager: UserManager
    ) { }

    public get router(): Router {
        const router: Router = Router();
        
        router.get("/:sessionId/:username",
            (req: Request, res: Response, next: NextFunction) => {
                // Send the request to the service and send the response
                //if(!this.userManager.verifySession(req.params.sessionId, req.params.username)) 
                  //  { res.json(403); return; };
                this.conversationService.getAll().then(conversations => {
                    res.json(conversations);
                })
                .catch(err => {
                    res.json(400);
                });
            });

        router.post("/:sessionId/:username/:conversationName",
            (req: Request, res: Response, next: NextFunction) => {
                //if(!this.userManager.verifySession(req.params.sessionId, req.params.username))
                  //  { res.json(403); return; }
                
                this.conversationService.create(req.params.conversationName, req.params.username).then(conversation => {
                    res.json(conversation)
                }).catch(err => {
                    if (err.name === 'MongoError' && err.code === 11000) {
                        // Duplicate conv name
                        res.json(409);
                    }
                    else {
                        res.json(400);
                    }
                });
            });
            
        return router;
    }
}