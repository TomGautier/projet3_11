import { Document } from 'mongoose';
import { IShapeConnection } from '../interfaces/shapeConnection';

export interface IShapeConnectionModel extends IShapeConnection, Document {
  // custom methods for your model would be defined here
}
