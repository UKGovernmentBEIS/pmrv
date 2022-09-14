import { TaskItem } from '../task-list.interface';

export const tasks: TaskItem<any>[] = [
  {
    type: 'typeA',
    linkText: 'Type A',
    status: 'not started',
    link: 'task-a',
  },
  {
    type: 'typeB',
    linkText: 'Type B',
    status: 'cannot start yet',
    link: 'task-b',
  },
  {
    type: 'typeC',
    linkText: 'Type C',
    status: 'in progress',
    link: 'task-c',
  },
  {
    type: 'typeD',
    linkText: 'Type D',
    status: 'incomplete',
    link: 'task-d',
  },
  {
    type: 'typeE',
    linkText: 'Type E',
    status: 'complete',
    link: 'task-e',
  },
  {
    type: 'typeF',
    linkText: 'Type E',
    status: 'complete',
    link: 'task-e',
  },
];
