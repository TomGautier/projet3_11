import { injectable, inject } from "inversify";
import { Router, Request, Response, NextFunction } from "express";

import { TYPES } from "../types";
import { UserManager } from "../services/user.manager";
import { ImageControllerInterface } from "../interfaces";
import { ImageService } from "../services/image.service";
import { DrawingSessionManager } from "../services/drawingSession.manager";

@injectable()
export class ImageController implements ImageControllerInterface {
    
    public constructor(
        @inject(TYPES.ImageServiceInterface) private imageService: ImageService,
        @inject(TYPES.UserManager) private userManager: UserManager
    ) { }

    public get router(): Router {
        const router: Router = Router();
            
        router.get("/single/:sessionId/:username/:imageId",
            (req: Request, res: Response, next: NextFunction) => {
                // Send the request to the service and send the response
                if(!this.userManager.verifySession(req.params.sessionId, req.params.username)) 
                    { res.json(403); return; }
                
                this.imageService.getById(req.params.imageId).then(image => {
                    res.json(image);
                });
            });

        router.get("/:sessionId/:username",
            (req: Request, res: Response, next: NextFunction) => {
                // Send the request to the service and send the response
                if(!this.userManager.verifySession(req.params.sessionId, req.params.username)) 
                    { res.json(403); return; }
                
                this.imageService.getAllByAuthor(req.params.username).then(image => {
                    res.json(image);
                });
            });
            
        router.get("/common/:sessionId/:username",
            (req: Request, res: Response, next: NextFunction) => {
                // Send the request to the service and send the response
                if(!this.userManager.verifySession(req.params.sessionId, req.params.username)) 
                    { res.json(403); return; }

                this.imageService.getAll().then(image => {
                    res.json(image);
                });
            }); 

        router.post("/offline/:sessionId/:username",
            (req: Request, res: Response, next: NextFunction) => {
                if(!this.userManager.verifySession(req.params.sessionId, req.params.username))
                    { res.json(403); return; }
            
                this.imageService.createMultiple(req.body.shapes)
                    .then(image => {
                        res.json(image);
                    });
        });

        router.post("/:sessionId/:username",
            (req: Request, res: Response, next: NextFunction) => {
                //if(!this.userManager.verifySession(req.params.sessionId, req.params.username))
                  //  { res.json(403); return; }
                
                this.imageService.create(req.body.imageId, 
                                        req.body.author,
                                        req.body.visibility,
                                        req.body.protection)
                    .then(image => {
                        res.json(image);
                    });
            });

        return router;
    }
}