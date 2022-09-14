<#import "template.ftl" as layout>
<@layout.registrationLayout displayRequiredFields=true displayMessage=!messagesPerField.existsError('totp') htmlPageTitle="${msg('loginTotpTitle')}"; section>

    <#if section = "header">
        ${msg("loginTotpTitle")}
    <#elseif section = "form">

        <p class="govuk-body">
            You must download FreeOTP Authenticator onto your smartphone. This app will be used to generate 6-digit
            verification codes when you log in to the PMRV or perform certain actions.
        </p>

        <br />

        <p class="govuk-body">
            If you need help with this, or downloading and using the app, view the PMRV
            <a id="guidance-link" class="govuk-link" data-toggle="modal" data-target="modal-default" href="#">guidance for FreeOTP
                Authenticator</a>.
        </p>
        <div class="gem-c-modal-dialogue" data-module="modal-dialogue" id="modal-default" style="display: none;">
            <dialog class="gem-c-modal-dialogue__box" aria-modal="true" role="dialog">
                <div id="mask" class="popover-mask" style="opacity: 1; display: block;">
                    <div id="popup" class="popup help-system" style="display: block;">
                        <p class="close">
                            <button class="gem-c-modal-dialogue__close-button gem-c-modal-dialogue__close-button-custom2" aria-label="Close modal dialogue">
                                <img src="${url.resourcesPath}/img/icon-close.png" alt="close modal">
                            </button>
                        </p>
                        <div id="popup-content">
                            <div id="help-system" class="help-system" tabindex="0">
                                <span class="govuk-heading-m govuk-heading-m-custom">PMRV guidance - FreeOTP Authenticator</span>
                                <div class="grid-row help-body">
                                    <div class="column-right">
                                            <nav class="help-navigation help-navigation-custom" aria-labelledby="help-navigation-heading" id="help-navigation">
                                                <h2 class="help-navigation-title" id="help-navigation-heading">Guidance topics</h2>
                                                <ul class="help-topics" role="tablist">
                                                    <li role="tab" aria-current="true" id="topic-accessing-the-registry-link" class="active">
                                                        <a href="#/" class="govuk-link" tabindex="0">Accessing the PMRV</a>
                                                    </li>
                                                    <li role="tab" aria-current="false" id="topic-downloading-the-app-link" class="non-active">
                                                        <a href="#/" class="govuk-link" tabindex="0">Downloading the app</a>
                                                    </li>
                                                    <li role="tab" aria-current="false" id="topic-for-apple-ios-link" class="non-active">
                                                        <a href="#/" class="govuk-link" tabindex="0">For Apple iOS</a>
                                                    </li>
                                                    <li role="tab"  aria-current="false"id="topic-for-android-link" class="non-active">
                                                        <a href="#/" class="govuk-link" tabindex="0">For Android</a>
                                                    </li>
                                                </ul>
                                            </nav>
                                        </div>
                                    <div class="column-left">
                                        <div class="help-sections" id="help-sections" tabindex="-1">
                                            <div  tabindex="0" role="tabpanel" aria-labelledby="topic-accessing-the-registry-link" class="topic-accessing-the-registry topic-accessing-the-registry-target">
                                                <h2 tabindex="-1">Accessing the PMRV</h2>
                                                <p>
                                                FreeOTP Authenticator is a two-factor authentication app that generates
                                                one time passcodes to add an additional level of security for online accounts.
                                                You’ll need to use the app to access the PMRV and perform certain actions.
                                                </p>
                                            </div>
                                            <div  tabindex="0" role="tabpanel" aria-labelledby="topic-downloading-the-app-link" class="topic-downloading-the-app">
                                                <h2 tabindex="-1">Downloading the app</h2>
                                                <p>
                                                    When you create a PMRV sign in, you must download the FreeOTP Authenticator app onto your smartphone. 
                                                    If you cannot download apps onto your phone, contact your organization's IT helpdesk.
                                                </p>
                                            </div>
                                            <div  tabindex="0" role="tabpanel" aria-labelledby="topic-for-apple-ios-link" class="topic-for-apple-ios">
                                                <h2 tabindex="-1">For Apple iOS</h2>
                                                <ol>
                                                    <li>
                                                        <p>On your iPhone, tap the App Store icon.</p>
                                                        <img src="${url.resourcesPath}/img/ios-apple.png" width="133" height="125" alt="">
                                                    </li>
                                                    <li>
                                                        <p>Search for FreeOTP Authenticator by Red Hat.</p>
                                                        <img src="${url.resourcesPath}/img/free-otp-authenticator-ios-app.png" width="470" height="227" alt="">
                                                    </li>
                                                    <li>
                                                        Tap the app, then download and install it.
                                                    </li>
                                                    <li>
                                                        You’ll be asked to allow FreeOTP Authenticator to access your camera. Select ‘Yes’.
                                                    </li>
                                                </ol>

                                                <h3 tabindex="-1" class="govuk-heading-m">Creating your PMRV sign in</h3>
                                                <p>
                                                    During the creation of your PMRV sign in you will be asked to
                                                    scan a QR code and follow further instructions on the page.
                                                </p>
                                                <ol>
                                                    <li>
                                                        <p>
                                                            Open the FreeOTP Authenticator app. At the top of the screen
                                                            select the QR code icon.
                                                        </p>
                                                        <img src="${url.resourcesPath}/img/free-otp-icon.png" width="410" height="50" alt="">
                                                    </li>
                                                    <li>
                                                        <p>
                                                            Scan the code shown on the screen using the rear facing camera on
                                                            your iPhone. If your camera does not activate, check your settings for
                                                            the FreeOTP Authenticator app and set the slider to allow access to the camera.
                                                        </p>
                                                        <img src="${url.resourcesPath}/img/free-otp-apps.png" width="410" height="250" alt="">
                                                    </li>
                                                    <li>
                                                        <p>
                                                            After scanning the QR code you will be asked to choose an icon. You can use
                                                            any icon shown on the app. This icon is specific to the PMRV and will be shown
                                                            whenever you open FreeOTP Authenticator. Tap on an icon to select it.
                                                        </p>
                                                        <img src="${url.resourcesPath}/img/ios-apps.png" width="410" height="676" alt="">
                                                    </li>
                                                    <li>
                                                        <p>After you have chosen an icon select 'Next' at the top of the screen.</p>
                                                        <img src="${url.resourcesPath}/img/ios-more-icons-menu.png" width="410" height="845" alt="">
                                                    </li>
                                                    <li>
                                                        <p>
                                                            You will be asked to choose whether you need to unlock your phone before
                                                            being able to activate the token. Set the slider to your choice, then select
                                                            'Next' at the top of the screen.
                                                        </p>
                                                        <img src="${url.resourcesPath}/img/free-otp-info.png" width="410" height="600" alt="">
                                                    </li>
                                                    <li>
                                                        <p>
                                                            Tap on the icon you chose for the PMRV to generate your 6-digit code.
                                                            You will have 60 seconds to enter your code into the PMRV before it changes.
                                                        </p>
                                                        <img src="${url.resourcesPath}/img/uk-pmrv-icon.png" width="268" height="133" alt="">
                                                    </li>
                                                </ol>

                                                <h3 tabindex="-1" class="govuk-heading-m">Using the app</h3>
                                                <ul>
                                                    <li>
                                                        <p>Use FreeOTP Authenticator whenever you are asked to enter a 6-digit code.</p>
                                                        <img src="${url.resourcesPath}/img/enter-6-digit.png" width="545" height="120" alt="">
                                                    </li>
                                                    <li>
                                                        <p>
                                                            If the code does not work, remove the token by swiping the icon from
                                                            left to right. Select ‘Remove token’, then rescan the QR code.
                                                        </p>
                                                        <img src="${url.resourcesPath}/img/remove-token.png" width="525" height="253" alt="">
                                                    </li>
                                                </ul>
                                                    <a
                                                            class="govuk-link app-c-back-to-top dont-print govuk-link--no-visited-state"
                                                            href="#"
                                                            onkeypress="backToTop(document.querySelector('#mask'));"
                                                            onclick="backToTop(document.querySelector('#mask'));"
                                                    >
                                                        <svg
                                                                class="app-c-back-to-top__icon"
                                                                xmlns="http://www.w3.org/2000/svg"
                                                                width="13"
                                                                height="17"
                                                                viewBox="0 0 13 17"
                                                        >
                                                            <path
                                                                    fill="currentColor"
                                                                    d="M6.5 0L0 6.5 1.4 8l4-4v12.7h2V4l4.3 4L13 6.4z"
                                                            ></path>
                                                        </svg>
                                                        Back to top
                                                    </a>
                                            </div>
                                            <div  tabindex="0" role="tabpanel" aria-labelledby="topic-for-android-link" class="topic-for-android">
                                                <h2 tabindex="-1">For Android</h2>
                                                <ol class="govuk-list govuk-list--number">
                                                    <li>
                                                        <p>On your phone, tap the Google Play Store icon.</p>
                                                        <img src="${url.resourcesPath}/img/google-play.png" alt=""/>
                                                    </li>
                                                    <li>
                                                        <p>Search for FreeOTP Authenticator by Red Hat.</p>
                                                        <img class="guidance-img" src="${url.resourcesPath}/img/free-otp.png" width="540" height="215" alt=""/>
                                                    </li>
                                                    <li>
                                                        Tap the app, then download and install it.
                                                    </li>
                                                    <li>
                                                        <p>You’ll be asked to allow FreeOTP Authenticator to access your camera and files. Select ‘Accept’.</p>
                                                        <img class="guidance-img" src="${url.resourcesPath}/img/free-otp-question.png" alt=""/>
                                                    </li>
                                                    <li>
                                                        <p>Set the sliders to allow access.</p>
                                                        <img class="guidance-img" src="${url.resourcesPath}/img/free-otp-allow.png" alt=""/>
                                                    </li>
                                                    <li>
                                                        <p>Depending on your Android version you may see a message that the app was built for an older version of Android. Select ‘OK’.</p>
                                                        <img class="guidance-img" src="${url.resourcesPath}/img/free-otp-updates.png" alt=""/>
                                                    </li>
                                                </ol>

                                                <h3 class="govuk-heading-m">Creating your PMRV sign in</h3>
                                                <p>When you create a PMRV sign in you will be asked to scan a QR code and follow further instructions on the page.</p>
                                                <ol class="govuk-list govuk-list--number">
                                                    <li>
                                                        <p>Open the FreeOTP Authenticator app. At the top of the screen select the QR code icon.</p>
                                                        <img class="guidance-img" src="${url.resourcesPath}/img/free-otp-select-qr.png" alt=""/>
                                                    </li>
                                                    <li>
                                                        Scan the code shown on the screen using the rear camera on your phone.
                                                    </li>
                                                    <li>
                                                        <p>If you are unable to scan the code, select the key icon in the app, and select ‘Problems with scanning’ on the PMRV page.</p>
                                                        <img class="guidance-img" src="${url.resourcesPath}/img/free-otp-header.png" alt=""/>
                                                        <br>
                                                        <img class="guidance-img" src="${url.resourcesPath}/img/free-otp-problem.png" alt=""/>
                                                    </li>
                                                    <li>
                                                        <p>Enter the information that you see on the PMRV page.</p>
                                                        <img class="guidance-img" src="${url.resourcesPath}/img/free-otp-token.png" alt=""/>
                                                    </li>
                                                    <li>
                                                        <p>
                                                            Tap the icon for the email address you used to create your PMRV sign in. This will generate your 6-digit code.
                                                            You will have 60 seconds to enter your code into the PMRV before it changes.
                                                        </p>
                                                        <img class="guidance-img" src="${url.resourcesPath}/img/free-otp-token-timer.png" alt=""/>
                                                    </li>
                                                </ol>

                                                <h3 class="govuk-heading-m">Using the app</h3>
                                                <ul class="govuk-list govuk-list--bullet">
                                                    <li>
                                                        <p>Use FreeOTP Authenticator whenever you are asked to enter a 6-digit code.</p>
                                                        <img class="guidance-img" src="${url.resourcesPath}/img/enter-token.png" width="545" hright="120" alt=""/>
                                                    </li>
                                                    <li>
                                                        <p>
                                                            If the code does not work, remove the token by selecting the three dots on the bottom right.
                                                            Select ‘Delete’, then rescan the QR code or enter the ‘Problems with scanning?’ details again.
                                                        </p>
                                                        <img class="guidance-img" src="${url.resourcesPath}/img/edit-token.png" alt=""/>
                                                    </li>
                                                </ul>
                                                <a
                                                        class="govuk-link app-c-back-to-top dont-print govuk-link--no-visited-state"
                                                        href="#"
                                                        onkeypress="backToTop(document.querySelector('#mask'));"
                                                        onclick="backToTop(document.querySelector('#mask'));"
                                                >
                                                    <svg
                                                            class="app-c-back-to-top__icon"
                                                            xmlns="http://www.w3.org/2000/svg"
                                                            width="13"
                                                            height="17"
                                                            viewBox="0 0 13 17"
                                                    >
                                                        <path
                                                                fill="currentColor"
                                                                d="M6.5 0L0 6.5 1.4 8l4-4v12.7h2V4l4.3 4L13 6.4z"
                                                        ></path>
                                                    </svg>
                                                    Back to top
                                                </a>
                                            </div>
                                            <a href="#help-navigation" class="link-back hidden">Back to topics</a>
                                        </div>
                                    </div>                                   
                                </div>

                                <div class="help-footer" data-cy="help-footer">
                                    <p class="flush--bottom" data-cy="help-footer-contacts">Need help? Email us at 
                                    </p>
                                    <hr>
                                    <button data-module="govuk-button" class="govuk-button govuk-button--secondary gem-c-modal-dialogue__close-button gem-c-modal-dialogue__close-button-bottom button-secondary gem-c-modal-dialogue__close-button-custom"
                                            aria-label="Close Guidance">Close Guidance
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </dialog>
        </div>

        <div class="govuk-warning-text">
            <span class="govuk-warning-text__icon" aria-hidden="true">!</span>
            <strong class="govuk-warning-text__text">
                <span class="govuk-warning-text__assistive">Warning</span>
                If you cannot provide a verification code you will not be able to access your account.
            </strong>
        </div>

        <ol id="kc-totp-settings" class="govuk-list govuk-list--number govuk-!-font-weight-bold">
            <li>
                <h2 class="govuk-body govuk-!-font-weight-bold">${msg("loginTotpStep1")}</h2>

                <h3 class="govuk-body govuk-!-font-weight-bold">${msg("totpIos")}</h3>
                <ol id="kc-totp-supported-apps-ios" class="govuk-list govuk-list--number">
                    <li>On your iPhone, tap the App store icon.</li>
                    <li>Search for FreeOTP Authenticator.</li>
                    <li>Tap the app, then download and install it.</li>
                </ol>
                <h3 class="govuk-body govuk-!-font-weight-bold">${msg("totpAndroid")}</h3>
                <ol id="kc-totp-supported-apps-android" class="govuk-list govuk-list--number">
                    <li>On your phone, tap the Google Play store icon.</li>
                    <li>Search for FreeOTP Authenticator.</li>
                    <li>Tap the app, then download and install it.</li>
                </ol>
            </li>

            <li>
                <h2 class="govuk-body govuk-!-font-weight-bold">${msg("loginTotpStep2")}</h2>


                <details class="govuk-details" data-module="govuk-details">
                    <summary class="govuk-details__summary">
                        <span class="govuk-details__summary-text">${msg("totpIos")}</span>
                    </summary>
                    <div class="govuk-details__text">
                        <ol id="kc-totp-scan-qr-ios" class="govuk-list govuk-list--number">
                            <li>Open the FreeOTP Authenticator app. At the top of the screen select the QR code icon</li>
                            <li>Scan the code below with the rear camera on your phone.</li>
                            <li>After scanning the QR code you will be asked to choose an icon. Tap on an icon to select it.
                                The icon is specific to the PMRV and will be shown whenever you open the app.
                            </li>
                            <li>After you have chosen an icon select 'Next' at the top of the screen</li>
                            <li>You will be asked to choose whether you need to unlock your phone before being able to
                                activate the token
                            </li>
                            <li>Set the slider to your choice, then select 'Next' at the top of the screen</li>
                            <li>Tap on the icon you chose for the PMRV to generate your 6-digit code.</li>
                        </ol>
                    </div>
                </details>

                <details class="govuk-details" data-module="govuk-details">
                    <summary class="govuk-details__summary">
                        <span class="govuk-details__summary-text">${msg("totpAndroid")}</span>
                    </summary>
                    <div class="govuk-details__text">
                        <ol id="kc-totp-scan-qr-android" class="govuk-list govuk-list--number">
                            <li>Open the FreeOTP Authenticator app. At the top of the screen select the QR code icon</li>
                            <li>Scan the code below with the rear camera on your phone.</li>
                            <li>After scanning the QR code an icon will appear for the email address you entered</li>
                            <li>Tap on the icon to generate your 6-digit code.</li>
                        </ol>
                    </div>
                </details>


                <img id="kc-totp-secret-qr-code" src="data:image/png;base64, ${totp.totpSecretQrCode}"
                     alt="Scan this QR Code"><br/>

                <details class="govuk-details" data-module="govuk-details">
                    <summary class="govuk-details__summary">
						<span class="govuk-details__summary-text">
						  ${msg("loginTotpUnableToScan")}
						</span>
                    </summary>
                    <div class="govuk-details__text">
                        <ol id="kc-totp-problems-with-scanning" class="govuk-list govuk-list--number">
                            <li>Select the key icon in the app</li>
                            <li>Enter the work email address you use to sign in to the PMRV</li>
                            <li>On the next line enter: ${msg("realmName")}</li>
                            <li>
                                Enter the following details:<br/>
                                <span id="kc-totp-secret-encoded">Secret:  ${totp.totpSecretEncoded}</span><br/>
                                <span id="kc-totp-type">Type: ${totp.policy.type}</span><br/>
                                <span id="kc-totp-digits">Digits: ${totp.policy.digits}</span><br/>
                                <span id="kc-totp-algorithm">Algorithm: ${totp.policy.getAlgorithmKey()}</span><br/>
                                <#if totp.policy.type = "totp">
                                    <span id="kc-totp-period">Interval: ${totp.policy.period}</span>
                                    <br/>
                                <#elseif totp.policy.type = "hotp">
                                    <span id="kc-totp-counter">Counter: ${totp.policy.initialCounter}</span>
                                    <br/>
                                </#if>
                            </li>
                            <li>Select 'Add'</li>
                        </ol>
                    </div>
                </details>
            </li>
            <li>
                <h2 class="govuk-body govuk-!-font-weight-bold">${msg("loginTotpStep3")}</h2>
            </li>
        </ol>

        <form action="${url.loginAction}" class="${properties.kcFormClass!}" id="kc-totp-settings-form" method="post">
            <#assign hasError = (message?has_content && message.summary?has_content && message.summary != msg("configureTotpMessage") && (message.type != 'warning' || !isAppInitiatedAction??))>
            <div class="${properties.kcFormGroupClass!} ${hasError?then('govuk-form-group--error', '')}">
                <#if hasError >
                    <span class="govuk-error-message" role="alert">
                    ${kcSanitize(message.summary)?no_esc}
                </span>
                </#if>
                <div id="totp-hint" class="govuk-hint">${msg("loginTotpAuthenticatorAppCode")}</div>
                <div class="${properties.kcInputWrapperClass!}">
                    <input type="text" id="otpCode" name="totp" autocomplete="off"
                           class="${properties.kcInputClass!} govuk-input--width-10" aria-describedby="totp-hint"/>
                </div>
                <input type="hidden" id="totpSecret" name="totpSecret" value="${totp.totpSecret}"/>

                <#if mode??><input type="hidden" id="mode" name="mode" value="${mode}"/></#if>
            </div>

            <#if isAppInitiatedAction??>
                <input type="submit"
                       class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}"
                       id="saveTOTPBtn" value="${msg("doSubmit")}"
                />
                <button type="submit"
                        class="${properties.kcButtonClass!} ${properties.kcButtonDefaultClass!} ${properties.kcButtonLargeClass!} ${properties.kcButtonLargeClass!}"
                        id="cancelTOTPBtn" name="cancel-aia" value="true" />${msg("doCancel")}
                </button>
            <#else>
                <input type="submit"
                       class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                       id="saveTOTPBtn" value="${msg("doContinue")}"
                />
            </#if>
        </form>
    </#if>
</@layout.registrationLayout>