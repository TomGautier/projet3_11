import { Document } from 'mongoose';
import { IConversation } from '../interfaces/conversation';

export interface IConversationModel extends IConversation, Document {
  // custom methods for your model would be defined here
}
