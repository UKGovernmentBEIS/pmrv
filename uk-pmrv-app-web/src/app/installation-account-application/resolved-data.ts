import { LegalEntityInfoDTO, RequestActionDTO } from 'pmrv-api';

export interface ResolvedData {
  task: RequestActionDTO[];
  application?: RequestActionDTO;
  legalEntities?: LegalEntityInfoDTO[];
}
