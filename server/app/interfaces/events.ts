import { injectable } from 'inversify';
import { EventEmitter } from 'events';
import { Logger } from '../services/logger.service';

@injectable()
export class UnsaucedEventEmitter {
    private eventEmitter: EventEmitter = new EventEmitter();
    public print(): void {
        Logger.debug('Events', `Number of listeners ${this.eventEmitter.listenerCount}`);
    }

    public on(event: string, action: ((...args: any[]) => any)) {
        Logger.debug('Events', 'New subscription for ' + event);
        this.eventEmitter.on(event, action);
    }

    public remove(event: string, action: ((...args: any[]) => any)) {
        this.eventEmitter.removeListener(event, action);
    }

    public emit(event: string, ...args: any[]) {
        this.eventEmitter.emit(event, args);
        console.log("EVENT :" + event);
    }
}