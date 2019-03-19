import { injectable, inject } from "inversify";
import { Router, Request, Response, NextFunction } from "express";

import { TYPES } from "../types";
import { ConnectionManager } from "../services/connection.service";
import { ImageControllerInterface } from "../interfaces";
import { ImageService } from "../services/image.service";

@injectable()
export class ImageController implements ImageControllerInterface {
    
    public constructor(
        @inject(TYPES.ImageServiceInterface) private imageService: ImageService,
        @inject(TYPES.ConnectionManager) private connectionManager: ConnectionManager
    ) { }

    public get router(): Router {
        const router: Router = Router();
            
        router.get("/single/:sessionId/:username/:imageId",
            (req: Request, res: Response, next: NextFunction) => {
                // Send the request to the service and send the response
                if(!this.connectionManager.verifySession(req.params.sessionId, req.params.username)) 
                    { res.json(403); return; }
                
                this.imageService.getById(req.params.imageId).then(image => {
                    res.json(image);
                });
            });

        router.get("/:sessionId/:username",
            (req: Request, res: Response, next: NextFunction) => {
                // Send the request to the service and send the response
                if(!this.connectionManager.verifySession(req.params.sessionId, req.params.username)) 
                    { res.json(403); return; }
                
                this.imageService.getAllByAuthor(req.params.username).then(image => {
                    res.json(image);
                });
            });
            
        router.get("/common/:sessionId/:username",
            (req: Request, res: Response, next: NextFunction) => {
                // Send the request to the service and send the response
                if(!this.connectionManager.verifySession(req.params.sessionId, req.params.username)) 
                    { res.json(403); return; }

                this.imageService.getAll().then(image => {
                    res.json(image);
                });
            }); 

        router.post("/:sessionId/:username",
            (req: Request, res: Response, next: NextFunction) => {
                if(!this.connectionManager.verifySession(req.params.sessionId, req.params.username))
                    { res.json(403); return; }
                
                this.imageService.create(req.body.author,
                                        req.body.visibility,
                                        req.body.protection,
                                        req.body.shapes)
                    .then(image => {
                        res.json(image);
                    });
            });

        return router;
    }
}