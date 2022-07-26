/**
 * PMRV API Documentation
 * Back-end REST API documentation for the UK PMRV application
 *
 * The version of the OpenAPI document: uk-pmrv-app-api 0.58.0-SNAPSHOT
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { HttpClient, HttpEvent, HttpHeaders, HttpParameterCodec, HttpParams, HttpResponse } from '@angular/common/http';
import { Inject, Injectable, Optional } from '@angular/core';

import { Observable } from 'rxjs';

import { Configuration } from '../configuration';
import { CustomHttpParameterCodec } from '../encoder';
import { AuthorityManagePermissionDTO } from '../model/authorityManagePermissionDTO';
import { RegulatorUsersAuthoritiesInfoDTO } from '../model/regulatorUsersAuthoritiesInfoDTO';
import { RegulatorUserUpdateStatusDTO } from '../model/regulatorUserUpdateStatusDTO';
import { BASE_PATH } from '../variables';

@Injectable({
  providedIn: 'root',
})
export class RegulatorAuthoritiesService {
  protected basePath = 'http://localhost:8080';
  public defaultHeaders = new HttpHeaders();
  public configuration = new Configuration();
  public encoder: HttpParameterCodec;

  constructor(
    protected httpClient: HttpClient,
    @Optional() @Inject(BASE_PATH) basePath: string,
    @Optional() configuration: Configuration,
  ) {
    if (configuration) {
      this.configuration = configuration;
    }
    if (typeof this.configuration.basePath !== 'string') {
      if (typeof basePath !== 'string') {
        basePath = this.basePath;
      }
      this.configuration.basePath = basePath;
    }
    this.encoder = this.configuration.encoder || new CustomHttpParameterCodec();
  }

  private addToHttpParams(httpParams: HttpParams, value: any, key?: string): HttpParams {
    if (typeof value === 'object' && value instanceof Date === false) {
      httpParams = this.addToHttpParamsRecursive(httpParams, value);
    } else {
      httpParams = this.addToHttpParamsRecursive(httpParams, value, key);
    }
    return httpParams;
  }

  private addToHttpParamsRecursive(httpParams: HttpParams, value?: any, key?: string): HttpParams {
    if (value == null) {
      return httpParams;
    }

    if (typeof value === 'object') {
      if (Array.isArray(value)) {
        (value as any[]).forEach((elem) => (httpParams = this.addToHttpParamsRecursive(httpParams, elem, key)));
      } else if (value instanceof Date) {
        if (key != null) {
          httpParams = httpParams.append(key, (value as Date).toISOString().substr(0, 10));
        } else {
          throw Error('key may not be null if value is Date');
        }
      } else {
        Object.keys(value).forEach(
          (k) => (httpParams = this.addToHttpParamsRecursive(httpParams, value[k], key != null ? `${key}.${k}` : k)),
        );
      }
    } else if (key != null) {
      httpParams = httpParams.append(key, value);
    } else {
      throw Error('key may not be null if value is not object or array');
    }
    return httpParams;
  }

  /**
   * Delete the current regulator user
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public deleteCurrentRegulatorUserByCompetentAuthorityUsingDELETE(): Observable<any>;
  public deleteCurrentRegulatorUserByCompetentAuthorityUsingDELETE(
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: undefined },
  ): Observable<HttpResponse<any>>;
  public deleteCurrentRegulatorUserByCompetentAuthorityUsingDELETE(
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: undefined },
  ): Observable<HttpEvent<any>>;
  public deleteCurrentRegulatorUserByCompetentAuthorityUsingDELETE(
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: undefined },
  ): Observable<any>;
  public deleteCurrentRegulatorUserByCompetentAuthorityUsingDELETE(
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: undefined },
  ): Observable<any> {
    let headers = this.defaultHeaders;

    let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
    if (httpHeaderAcceptSelected === undefined) {
      // to determine the Accept header
      const httpHeaderAccepts: string[] = [];
      httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    }
    if (httpHeaderAcceptSelected !== undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }

    let responseType_: 'text' | 'json' = 'json';
    if (httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
      responseType_ = 'text';
    }

    return this.httpClient.delete<any>(`${this.configuration.basePath}/api/v1.0/regulator-authorities`, {
      responseType: <any>responseType_,
      withCredentials: this.configuration.withCredentials,
      headers: headers,
      observe: observe,
      reportProgress: reportProgress,
    });
  }

  /**
   * Delete the regulator user that corresponds to the provided user id
   * @param userId The regulator to be deleted
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public deleteRegulatorUserByCompetentAuthorityUsingDELETE(userId: string): Observable<any>;
  public deleteRegulatorUserByCompetentAuthorityUsingDELETE(
    userId: string,
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: undefined },
  ): Observable<HttpResponse<any>>;
  public deleteRegulatorUserByCompetentAuthorityUsingDELETE(
    userId: string,
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: undefined },
  ): Observable<HttpEvent<any>>;
  public deleteRegulatorUserByCompetentAuthorityUsingDELETE(
    userId: string,
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: undefined },
  ): Observable<any>;
  public deleteRegulatorUserByCompetentAuthorityUsingDELETE(
    userId: string,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: undefined },
  ): Observable<any> {
    if (userId === null || userId === undefined) {
      throw new Error(
        'Required parameter userId was null or undefined when calling deleteRegulatorUserByCompetentAuthorityUsingDELETE.',
      );
    }

    let headers = this.defaultHeaders;

    let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
    if (httpHeaderAcceptSelected === undefined) {
      // to determine the Accept header
      const httpHeaderAccepts: string[] = [];
      httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    }
    if (httpHeaderAcceptSelected !== undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }

    let responseType_: 'text' | 'json' = 'json';
    if (httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
      responseType_ = 'text';
    }

    return this.httpClient.delete<any>(
      `${this.configuration.basePath}/api/v1.0/regulator-authorities/${encodeURIComponent(String(userId))}`,
      {
        responseType: <any>responseType_,
        withCredentials: this.configuration.withCredentials,
        headers: headers,
        observe: observe,
        reportProgress: reportProgress,
      },
    );
  }

  /**
   * Retrieves the users of type REGULATOR
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public getCaRegulatorsUsingGET(): Observable<RegulatorUsersAuthoritiesInfoDTO>;
  public getCaRegulatorsUsingGET(
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<HttpResponse<RegulatorUsersAuthoritiesInfoDTO>>;
  public getCaRegulatorsUsingGET(
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<HttpEvent<RegulatorUsersAuthoritiesInfoDTO>>;
  public getCaRegulatorsUsingGET(
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<RegulatorUsersAuthoritiesInfoDTO>;
  public getCaRegulatorsUsingGET(
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<any> {
    let headers = this.defaultHeaders;

    let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
    if (httpHeaderAcceptSelected === undefined) {
      // to determine the Accept header
      const httpHeaderAccepts: string[] = ['*/*'];
      httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    }
    if (httpHeaderAcceptSelected !== undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }

    let responseType_: 'text' | 'json' = 'json';
    if (httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
      responseType_ = 'text';
    }

    return this.httpClient.get<RegulatorUsersAuthoritiesInfoDTO>(
      `${this.configuration.basePath}/api/v1.0/regulator-authorities`,
      {
        responseType: <any>responseType_,
        withCredentials: this.configuration.withCredentials,
        headers: headers,
        observe: observe,
        reportProgress: reportProgress,
      },
    );
  }

  /**
   * Retrieves the current regulator user\&#39;s permissions
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public getCurrentRegulatorUserPermissionsByCaUsingGET(): Observable<AuthorityManagePermissionDTO>;
  public getCurrentRegulatorUserPermissionsByCaUsingGET(
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<HttpResponse<AuthorityManagePermissionDTO>>;
  public getCurrentRegulatorUserPermissionsByCaUsingGET(
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<HttpEvent<AuthorityManagePermissionDTO>>;
  public getCurrentRegulatorUserPermissionsByCaUsingGET(
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<AuthorityManagePermissionDTO>;
  public getCurrentRegulatorUserPermissionsByCaUsingGET(
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<any> {
    let headers = this.defaultHeaders;

    let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
    if (httpHeaderAcceptSelected === undefined) {
      // to determine the Accept header
      const httpHeaderAccepts: string[] = ['*/*'];
      httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    }
    if (httpHeaderAcceptSelected !== undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }

    let responseType_: 'text' | 'json' = 'json';
    if (httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
      responseType_ = 'text';
    }

    return this.httpClient.get<AuthorityManagePermissionDTO>(
      `${this.configuration.basePath}/api/v1.0/regulator-authorities/permissions`,
      {
        responseType: <any>responseType_,
        withCredentials: this.configuration.withCredentials,
        headers: headers,
        observe: observe,
        reportProgress: reportProgress,
      },
    );
  }

  /**
   * Retrieves the regulator permissions group levels
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public getRegulatorPermissionGroupLevelsUsingGET(): Observable<{ [key: string]: Array<string> }>;
  public getRegulatorPermissionGroupLevelsUsingGET(
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<HttpResponse<{ [key: string]: Array<string> }>>;
  public getRegulatorPermissionGroupLevelsUsingGET(
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<HttpEvent<{ [key: string]: Array<string> }>>;
  public getRegulatorPermissionGroupLevelsUsingGET(
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<{ [key: string]: Array<string> }>;
  public getRegulatorPermissionGroupLevelsUsingGET(
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<any> {
    let headers = this.defaultHeaders;

    let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
    if (httpHeaderAcceptSelected === undefined) {
      // to determine the Accept header
      const httpHeaderAccepts: string[] = ['*/*'];
      httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    }
    if (httpHeaderAcceptSelected !== undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }

    let responseType_: 'text' | 'json' = 'json';
    if (httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
      responseType_ = 'text';
    }

    return this.httpClient.get<{ [key: string]: Array<string> }>(
      `${this.configuration.basePath}/api/v1.0/regulator-authorities/permissions/group-levels`,
      {
        responseType: <any>responseType_,
        withCredentials: this.configuration.withCredentials,
        headers: headers,
        observe: observe,
        reportProgress: reportProgress,
      },
    );
  }

  /**
   * Retrieves the regulator user\&#39;s permissions
   * @param userId The regulator user id
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public getRegulatorUserPermissionsByCaAndIdUsingGET(userId: string): Observable<AuthorityManagePermissionDTO>;
  public getRegulatorUserPermissionsByCaAndIdUsingGET(
    userId: string,
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<HttpResponse<AuthorityManagePermissionDTO>>;
  public getRegulatorUserPermissionsByCaAndIdUsingGET(
    userId: string,
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<HttpEvent<AuthorityManagePermissionDTO>>;
  public getRegulatorUserPermissionsByCaAndIdUsingGET(
    userId: string,
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<AuthorityManagePermissionDTO>;
  public getRegulatorUserPermissionsByCaAndIdUsingGET(
    userId: string,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<any> {
    if (userId === null || userId === undefined) {
      throw new Error(
        'Required parameter userId was null or undefined when calling getRegulatorUserPermissionsByCaAndIdUsingGET.',
      );
    }

    let headers = this.defaultHeaders;

    let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
    if (httpHeaderAcceptSelected === undefined) {
      // to determine the Accept header
      const httpHeaderAccepts: string[] = ['*/*'];
      httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    }
    if (httpHeaderAcceptSelected !== undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }

    let responseType_: 'text' | 'json' = 'json';
    if (httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
      responseType_ = 'text';
    }

    return this.httpClient.get<AuthorityManagePermissionDTO>(
      `${this.configuration.basePath}/api/v1.0/regulator-authorities/permissions/${encodeURIComponent(String(userId))}`,
      {
        responseType: <any>responseType_,
        withCredentials: this.configuration.withCredentials,
        headers: headers,
        observe: observe,
        reportProgress: reportProgress,
      },
    );
  }

  /**
   * Updates regulator users status
   * @param regulatorUserUpdateStatusDTO
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public updateCompetentAuthorityRegulatorUsersStatusUsingPOST(
    regulatorUserUpdateStatusDTO?: Array<RegulatorUserUpdateStatusDTO>,
  ): Observable<any>;
  public updateCompetentAuthorityRegulatorUsersStatusUsingPOST(
    regulatorUserUpdateStatusDTO: Array<RegulatorUserUpdateStatusDTO>,
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: undefined },
  ): Observable<HttpResponse<any>>;
  public updateCompetentAuthorityRegulatorUsersStatusUsingPOST(
    regulatorUserUpdateStatusDTO: Array<RegulatorUserUpdateStatusDTO>,
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: undefined },
  ): Observable<HttpEvent<any>>;
  public updateCompetentAuthorityRegulatorUsersStatusUsingPOST(
    regulatorUserUpdateStatusDTO: Array<RegulatorUserUpdateStatusDTO>,
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: undefined },
  ): Observable<any>;
  public updateCompetentAuthorityRegulatorUsersStatusUsingPOST(
    regulatorUserUpdateStatusDTO?: Array<RegulatorUserUpdateStatusDTO>,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: undefined },
  ): Observable<any> {
    let headers = this.defaultHeaders;

    let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
    if (httpHeaderAcceptSelected === undefined) {
      // to determine the Accept header
      const httpHeaderAccepts: string[] = [];
      httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    }
    if (httpHeaderAcceptSelected !== undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }

    // to determine the Content-Type header
    const consumes: string[] = ['application/json'];
    const httpContentTypeSelected: string | undefined = this.configuration.selectHeaderContentType(consumes);
    if (httpContentTypeSelected !== undefined) {
      headers = headers.set('Content-Type', httpContentTypeSelected);
    }

    let responseType_: 'text' | 'json' = 'json';
    if (httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
      responseType_ = 'text';
    }

    return this.httpClient.post<any>(
      `${this.configuration.basePath}/api/v1.0/regulator-authorities`,
      regulatorUserUpdateStatusDTO,
      {
        responseType: <any>responseType_,
        withCredentials: this.configuration.withCredentials,
        headers: headers,
        observe: observe,
        reportProgress: reportProgress,
      },
    );
  }
}
