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
         MessageControllerInterface,
         MessageServiceInterface} from "./interfaces";
import { SocketService } from "./services/socket.service";
import { UnsaucedEventEmitter } from "./interfaces/events";
import { DatabaseService, DatabaseConnection } from "./services/database.service";
import { ConversationManager } from "./services/conversation.manager";
import { LoginService } from "./services/login.service";
import { UserService } from "./services/user.service";
import { ConversationController } from "./controllers/conversation.controller";
import { ConversationService } from "./services/conversation.service";
import { MessageController } from "./controllers/message.controller";
import { MessageService } from "./services/message.service";

const container: Container = new Container();

container.bind<ServerInterface>(TYPES.ServerInterface).to(Server);

container.bind<ConversationControllerInterface>(TYPES.ConversationControllerInterface).to(ConversationController);
container.bind<ConversationServiceInterface>(TYPES.ConversationServiceInterface).to(ConversationService);
container.bind<MessageControllerInterface>(TYPES.MessageControllerInterface).to(MessageController);
container.bind<MessageServiceInterface>(TYPES.MessageServiceInterface).to(MessageService);

container.bind<ApplicationInterface>(TYPES.ApplicationInterface).to(Application);
container.bind<IndexControllerInterface>(TYPES.IndexControllerInterface).to(IndexController);
container.bind<IndexServiceInterface>(TYPES.IndexServiceInterface).to(IndexService);
container.bind<DatabaseConnection>(TYPES.DatabaseConnection).to(DatabaseConnection).inSingletonScope();

container.bind<DateControllerInterface>(TYPES.DateControllerInterface).to(DateController);
container.bind<DateServiceInterface>(TYPES.DateServiceInterface).to(DateService);

container.bind<DatabaseService>(TYPES.DatabaseService).to(DatabaseService);
container.bind<SocketService>(TYPES.SocketService).to(SocketService).inSingletonScope();
container.bind<LoginService>(TYPES.LoginService).to(LoginService);
container.bind<ConversationManager>(TYPES.ConversationManager).to(ConversationManager);
container.bind<UserService>(TYPES.UserService).to(UserService);
container.bind<UnsaucedEventEmitter>(TYPES.EventEmitter).to(UnsaucedEventEmitter);

export { container };
