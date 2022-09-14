import {
  approachMapper,
  reviewSectionsCompletedStandardInitialized,
  sourceStreamMapper,
} from '@permit-application/shared/permit-sections/sections-completed-maps';

import { Permit } from 'pmrv-api';

export const initializeReviewSectionsCompleted = (permit: Permit) => {
  let reviewSectionsCompleted: { [x: string]: boolean } = reviewSectionsCompletedStandardInitialized;
  for (const [key, value] of Object.entries(permit)) {
    if (value && key === 'monitoringApproaches') {
      for (const k of Object.keys(value)) {
        reviewSectionsCompleted = { ...reviewSectionsCompleted, [k]: true };
      }
    }
  }
  return reviewSectionsCompleted;
};

export const initializePermitSectionsCompleted = (permit: Permit): { [x: string]: boolean[] } => {
  let sectionsCompleted: { [x: string]: boolean[] } = Object.assign({});
  let subSectionsCompleted: { [x: string]: boolean[] } = Object.assign({});

  for (const [key, value] of Object.entries(permit)) {
    if (value) {
      if (key === 'monitoringApproaches') {
        for (const k of Object.keys(value)) {
          subSectionsCompleted = {
            ...monitoringApproaches(value[k], subSectionsCompleted, approachMapper[k]),
            ...sourceStreamCategoryAppliedTiers(value[k], subSectionsCompleted, sourceStreamMapper[k]),
          };
        }
      }

      sectionsCompleted = {
        ...sectionsCompleted,
        ...subSectionsCompleted,
        [key]: [true],
        ['monitoringApproachesPrepare']: [true],
      };
    } else {
      sectionsCompleted = { ...sectionsCompleted, [key]: [false] };
    }
  }
  return sectionsCompleted;
};

const monitoringApproaches = (value: any, subSectionsCompleted: { [x: string]: boolean[] }, map: any) => {
  if (value) {
    for (const [k, values] of Object.entries(value)) {
      if (k !== 'type' && map && map[k]) {
        if (!Array.isArray(values)) {
          subSectionsCompleted = {
            ...subSectionsCompleted,
            [map[k]]: [true],
          };
        } else {
          for (const v of values) {
            if (v && k !== 'sourceStreamCategoryAppliedTiers') {
              if (!subSectionsCompleted[map[k]]) {
                subSectionsCompleted[map[k]] = [];
              }
              subSectionsCompleted[map[k]].push(true);
            }
          }
        }
      }
    }
    return subSectionsCompleted;
  }
};

const sourceStreamCategoryAppliedTiers = (value: any, subSectionsCompleted: { [x: string]: boolean[] }, map: any) => {
  if (value && value['sourceStreamCategoryAppliedTiers'] && map) {
    const soureStreams = value['sourceStreamCategoryAppliedTiers'];

    for (const source of soureStreams) {
      for (const [key, value] of Object.entries(source)) {
        if (value) {
          if (!subSectionsCompleted[map[key]]) {
            subSectionsCompleted[map[key]] = [];
          }
          subSectionsCompleted[map[key]].push(true);
        }
      }
    }
    return subSectionsCompleted;
  }
};
