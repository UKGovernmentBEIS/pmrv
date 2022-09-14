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
import { UsersAuthoritiesInfoDTO } from '../model/usersAuthoritiesInfoDTO';
import { VerifierAuthorityUpdateDTO } from '../model/verifierAuthorityUpdateDTO';
import { BASE_PATH } from '../variables';

@Injectable({
  providedIn: 'root',
})
export class VerifierAuthoritiesService {
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
   * Delete the current verifier user
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public deleteCurrentVerifierAuthorityUsingDELETE(): Observable<any>;
  public deleteCurrentVerifierAuthorityUsingDELETE(
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: undefined },
  ): Observable<HttpResponse<any>>;
  public deleteCurrentVerifierAuthorityUsingDELETE(
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: undefined },
  ): Observable<HttpEvent<any>>;
  public deleteCurrentVerifierAuthorityUsingDELETE(
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: undefined },
  ): Observable<any>;
  public deleteCurrentVerifierAuthorityUsingDELETE(
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

    return this.httpClient.delete<any>(`${this.configuration.basePath}/api/v1.0/verifier-authorities`, {
      responseType: <any>responseType_,
      withCredentials: this.configuration.withCredentials,
      headers: headers,
      observe: observe,
      reportProgress: reportProgress,
    });
  }

  /**
   * Delete the verifier user that corresponds to the provided user id
   * @param userId The regulator to be deleted
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public deleteVerifierAuthorityUsingDELETE(userId: string): Observable<any>;
  public deleteVerifierAuthorityUsingDELETE(
    userId: string,
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: undefined },
  ): Observable<HttpResponse<any>>;
  public deleteVerifierAuthorityUsingDELETE(
    userId: string,
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: undefined },
  ): Observable<HttpEvent<any>>;
  public deleteVerifierAuthorityUsingDELETE(
    userId: string,
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: undefined },
  ): Observable<any>;
  public deleteVerifierAuthorityUsingDELETE(
    userId: string,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: undefined },
  ): Observable<any> {
    if (userId === null || userId === undefined) {
      throw new Error(
        'Required parameter userId was null or undefined when calling deleteVerifierAuthorityUsingDELETE.',
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
      `${this.configuration.basePath}/api/v1.0/verifier-authorities/${encodeURIComponent(String(userId))}`,
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
   * Retrieves the list of verifier authorities related to the verification body
   * @param id The verification body id
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public getVerifierAuthoritiesByVerificationBodyIdUsingGET(id: number): Observable<UsersAuthoritiesInfoDTO>;
  public getVerifierAuthoritiesByVerificationBodyIdUsingGET(
    id: number,
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<HttpResponse<UsersAuthoritiesInfoDTO>>;
  public getVerifierAuthoritiesByVerificationBodyIdUsingGET(
    id: number,
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<HttpEvent<UsersAuthoritiesInfoDTO>>;
  public getVerifierAuthoritiesByVerificationBodyIdUsingGET(
    id: number,
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<UsersAuthoritiesInfoDTO>;
  public getVerifierAuthoritiesByVerificationBodyIdUsingGET(
    id: number,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<any> {
    if (id === null || id === undefined) {
      throw new Error(
        'Required parameter id was null or undefined when calling getVerifierAuthoritiesByVerificationBodyIdUsingGET.',
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

    return this.httpClient.get<UsersAuthoritiesInfoDTO>(
      `${this.configuration.basePath}/api/v1.0/verifier-authorities/vb/${encodeURIComponent(String(id))}`,
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
   * Retrieves the list of verifier authorities related to the verification body to which I have been assigned to
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public getVerifierAuthoritiesUsingGET(): Observable<UsersAuthoritiesInfoDTO>;
  public getVerifierAuthoritiesUsingGET(
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<HttpResponse<UsersAuthoritiesInfoDTO>>;
  public getVerifierAuthoritiesUsingGET(
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<HttpEvent<UsersAuthoritiesInfoDTO>>;
  public getVerifierAuthoritiesUsingGET(
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<UsersAuthoritiesInfoDTO>;
  public getVerifierAuthoritiesUsingGET(
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

    return this.httpClient.get<UsersAuthoritiesInfoDTO>(
      `${this.configuration.basePath}/api/v1.0/verifier-authorities`,
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
   * Updates the verifier authorities of the specified verification body
   * @param id The verification body id
   * @param verifierAuthorityUpdateDTO
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public updateVerifierAuthoritiesByVerificationBodyIdUsingPOST(
    id: number,
    verifierAuthorityUpdateDTO?: Array<VerifierAuthorityUpdateDTO>,
  ): Observable<any>;
  public updateVerifierAuthoritiesByVerificationBodyIdUsingPOST(
    id: number,
    verifierAuthorityUpdateDTO: Array<VerifierAuthorityUpdateDTO>,
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: undefined },
  ): Observable<HttpResponse<any>>;
  public updateVerifierAuthoritiesByVerificationBodyIdUsingPOST(
    id: number,
    verifierAuthorityUpdateDTO: Array<VerifierAuthorityUpdateDTO>,
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: undefined },
  ): Observable<HttpEvent<any>>;
  public updateVerifierAuthoritiesByVerificationBodyIdUsingPOST(
    id: number,
    verifierAuthorityUpdateDTO: Array<VerifierAuthorityUpdateDTO>,
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: undefined },
  ): Observable<any>;
  public updateVerifierAuthoritiesByVerificationBodyIdUsingPOST(
    id: number,
    verifierAuthorityUpdateDTO?: Array<VerifierAuthorityUpdateDTO>,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: undefined },
  ): Observable<any> {
    if (id === null || id === undefined) {
      throw new Error(
        'Required parameter id was null or undefined when calling updateVerifierAuthoritiesByVerificationBodyIdUsingPOST.',
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
      `${this.configuration.basePath}/api/v1.0/verifier-authorities/vb/${encodeURIComponent(String(id))}`,
      verifierAuthorityUpdateDTO,
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
   * Updates the verifier authorities
   * @param verifierAuthorityUpdateDTO
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public updateVerifierAuthoritiesUsingPOST(
    verifierAuthorityUpdateDTO?: Array<VerifierAuthorityUpdateDTO>,
  ): Observable<any>;
  public updateVerifierAuthoritiesUsingPOST(
    verifierAuthorityUpdateDTO: Array<VerifierAuthorityUpdateDTO>,
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: undefined },
  ): Observable<HttpResponse<any>>;
  public updateVerifierAuthoritiesUsingPOST(
    verifierAuthorityUpdateDTO: Array<VerifierAuthorityUpdateDTO>,
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: undefined },
  ): Observable<HttpEvent<any>>;
  public updateVerifierAuthoritiesUsingPOST(
    verifierAuthorityUpdateDTO: Array<VerifierAuthorityUpdateDTO>,
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: undefined },
  ): Observable<any>;
  public updateVerifierAuthoritiesUsingPOST(
    verifierAuthorityUpdateDTO?: Array<VerifierAuthorityUpdateDTO>,
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
      `${this.configuration.basePath}/api/v1.0/verifier-authorities`,
      verifierAuthorityUpdateDTO,
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
