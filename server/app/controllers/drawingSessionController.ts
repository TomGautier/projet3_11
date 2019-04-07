import { injectable, inject } from "inversify";
import { Router, Request, Response, NextFunction } from "express";

import { TYPES } from "../types";
import { UserManager } from "../services/user.manager";
import { DrawingSessionService } from "../services/drawingSession.service";
import { DrawingSessionControllerInterface } from "../interfaces";

@injectable()
export class DrawingSessionController implements DrawingSessionControllerInterface {
    
    public constructor(
        @inject(TYPES.DrawingSessionServiceInterface) private drawingSessionService: DrawingSessionService,
        @inject(TYPES.UserManager) private userManager: UserManager
    ) { }

    public get router(): Router {
        const router: Router = Router();
            
        router.get("/:sessionId/:username/:imageId",
            (req: Request, res: Response, next: NextFunction) => {
                // Send the request to the service and send the response
                if(!this.userManager.verifySession(req.params.sessionId, req.params.username)) 
                    { res.json(403); return; }
                
                this.drawingSessionService.getShapesByImage(req.params.imageId)
                    .then(shapes => {
                        res.json(shapes);
                    })
                    .catch(err => {
                        res.json({});
                    });
        });

        router.get("/connections/:sessionId/:username/:imageId",
            (req: Request, res: Response, next: NextFunction) => {
                // Send the request to the service and send the response
                if(!this.userManager.verifySession(req.params.sessionId, req.params.username)) 
                    { res.json(403); return; }
                
                this.drawingSessionService.getShapeConnectionsByImage(req.params.imageId)
                    .then(shapeConnections => {
                        res.json(shapeConnections);
                    })
                    .catch(err => {
                        res.json({});
                    });
        });

        return router;
    }
}