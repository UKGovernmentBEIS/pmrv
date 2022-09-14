import { TaskSection } from '../task-list.interface';
import { tasks } from './tasks';

export const sections: TaskSection<any>[] = [
  {
    type: 'sectionTypeA',
    title: 'Some section A',
    tasks: tasks.slice(0, 1),
  },
  {
    type: 'sectionTypeB',
    title: 'Some section B',
    tasks: tasks.slice(1, 4),
  },
  {
    type: 'sectionTypeC',
    title: 'Some section C',
    tasks: tasks.slice(4, 6),
  },
];
