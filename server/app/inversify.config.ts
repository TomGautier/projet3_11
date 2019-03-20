/* From : https://github.com/Aboisier/Projet2-Equipe6
* Written by : Dylan Farvacque
*/

import { Container } from "inversify";
import { TYPES } from "./types";
import { Server } from "./server";
import { Application } from "./app";
import { IndexController } from "./controllers/index.controller";
import { DateController } from "./controllers/date.controller";
import { IndexService } from "./services/index.service";
import { DateService } from "./services/date.service";


import { DateServiceInterface,
         IndexServiceInterface,
         DateControllerInterface, 
         IndexControllerInterface, 
         ServerInterface,
         ApplicationInterface, 
         ConversationControllerInterface,
         ConversationServiceInterface,
         ConnectionControllerInterface,
         ConnectionServiceInterface,
         DrawingSessionServiceInterface,
         ImageControllerInterface,
         ImageServiceInterface,
         UserServiceInterface,
UserControllerInterface} from "./interfaces";
import { SocketService } from "./services/socket.service";
import { UnsaucedEventEmitter } from "./interfaces/events";
import { DatabaseService, DatabaseConnection } from "./services/database.service";
import { ConversationManager} from "./services/conversation.manager";
import { ConnectionService, ConnectionManager } from "./services/connection.service";
import { UserService } from "./services/user.service";
import { ConversationController } from "./controllers/conversation.controller";
import { ConversationService } from "./services/conversation.service";
import { ConnectionController } from "./controllers/connection.controller";
import { DrawingSessionService } from "./services/drawingSession.service";
import { ImageController } from "./controllers/image.controller";
import { ImageService } from "./services/image.service";
import { DrawingSessionManager} from "./services/drawingSession.manager";
import { UserController } from "./controllers/user.controller";
import { UserManager } from "./services/user.manager";


const container: Container = new Container();

container.bind<ServerInterface>(TYPES.ServerInterface).to(Server);

container.bind<ConversationControllerInterface>(TYPES.ConversationControllerInterface).to(ConversationController);
container.bind<ConversationServiceInterface>(TYPES.ConversationServiceInterface).to(ConversationService);
container.bind<ConnectionControllerInterface>(TYPES.ConnectionControllerInterface).to(ConnectionController);
container.bind<ConnectionServiceInterface>(TYPES.ConnectionServiceInterface).to(ConnectionService);
container.bind<ImageControllerInterface>(TYPES.ImageControllerInterface).to(ImageController);
container.bind<ImageServiceInterface>(TYPES.ImageServiceInterface).to(ImageService);
container.bind<UserControllerInterface>(TYPES.UserControllerInterface).to(UserController);
container.bind<UserServiceInterface>(TYPES.UserServiceInterface).to(UserService);
container.bind<DrawingSessionServiceInterface>(TYPES.DrawingSessionServiceInterface).to(DrawingSessionService);

container.bind<ApplicationInterface>(TYPES.ApplicationInterface).to(Application);
container.bind<IndexControllerInterface>(TYPES.IndexControllerInterface).to(IndexController);
container.bind<IndexServiceInterface>(TYPES.IndexServiceInterface).to(IndexService);
container.bind<DatabaseConnection>(TYPES.DatabaseConnection).to(DatabaseConnection).inSingletonScope();

container.bind<DateControllerInterface>(TYPES.DateControllerInterface).to(DateController);
container.bind<DateServiceInterface>(TYPES.DateServiceInterface).to(DateService);

container.bind<DatabaseService>(TYPES.DatabaseService).to(DatabaseService);
container.bind<SocketService>(TYPES.SocketService).to(SocketService).inSingletonScope();
container.bind<ConversationManager>(TYPES.ConversationManager).to(ConversationManager);
container.bind<ConnectionManager>(TYPES.ConnectionManager).to(ConnectionManager).inSingletonScope();

container.bind<UserManager>(TYPES.UserManager).to(UserManager).inSingletonScope();
container.bind<DrawingSessionManager>(TYPES.DrawingSessionManager).to(DrawingSessionManager).inSingletonScope();
container.bind<UserService>(TYPES.UserService).to(UserService);
container.bind<UnsaucedEventEmitter>(TYPES.EventEmitter).to(UnsaucedEventEmitter);

export { container };
