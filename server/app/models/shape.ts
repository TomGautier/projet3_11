import { Document } from 'mongoose';
import { IShape } from '../interfaces/shape';

export interface IShapeModel extends IShape, Document {
  // custom methods for your model would be defined here
}
