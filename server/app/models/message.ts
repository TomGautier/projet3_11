import { Document } from 'mongoose';
import { IMessage } from '../interfaces/message';

export interface IMessageModel extends IMessage, Document {
  // custom methods for your model would be defined here
}
