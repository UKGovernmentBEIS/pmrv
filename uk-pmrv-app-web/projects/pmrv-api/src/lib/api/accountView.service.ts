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
import { AccountDetailsDTO } from '../model/accountDetailsDTO';
import { AccountHeaderInfoDTO } from '../model/accountHeaderInfoDTO';
import { BASE_PATH } from '../variables';

@Injectable({
  providedIn: 'root',
})
export class AccountViewService {
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
   * Get the account with the provided id
   * @param id The account id
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public getAccountByIdUsingGET(id: number): Observable<AccountDetailsDTO>;
  public getAccountByIdUsingGET(
    id: number,
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<HttpResponse<AccountDetailsDTO>>;
  public getAccountByIdUsingGET(
    id: number,
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<HttpEvent<AccountDetailsDTO>>;
  public getAccountByIdUsingGET(
    id: number,
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<AccountDetailsDTO>;
  public getAccountByIdUsingGET(
    id: number,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<any> {
    if (id === null || id === undefined) {
      throw new Error('Required parameter id was null or undefined when calling getAccountByIdUsingGET.');
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

    return this.httpClient.get<AccountDetailsDTO>(
      `${this.configuration.basePath}/api/v1.0/account/${encodeURIComponent(String(id))}`,
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
   * Get the account header info for the provided account
   * @param id The account id
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public getAccountHeaderInfoByIdUsingGET(id: number): Observable<AccountHeaderInfoDTO>;
  public getAccountHeaderInfoByIdUsingGET(
    id: number,
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<HttpResponse<AccountHeaderInfoDTO>>;
  public getAccountHeaderInfoByIdUsingGET(
    id: number,
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<HttpEvent<AccountHeaderInfoDTO>>;
  public getAccountHeaderInfoByIdUsingGET(
    id: number,
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<AccountHeaderInfoDTO>;
  public getAccountHeaderInfoByIdUsingGET(
    id: number,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<any> {
    if (id === null || id === undefined) {
      throw new Error('Required parameter id was null or undefined when calling getAccountHeaderInfoByIdUsingGET.');
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

    return this.httpClient.get<AccountHeaderInfoDTO>(
      `${this.configuration.basePath}/api/v1.0/account/${encodeURIComponent(String(id))}/header-info`,
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
