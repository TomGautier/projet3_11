import { Document } from 'mongoose';
import { IImage } from '../interfaces/image';

export interface IImageModel extends IImage, Document {
  // custom methods for your model would be defined here
}
