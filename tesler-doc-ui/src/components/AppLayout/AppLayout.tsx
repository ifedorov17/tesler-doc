/*
 * TESLERDOC - UI
 * Copyright (C) 2020 Tesler Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from 'react'
import {Dispatch} from 'redux'
import {Layout as AntLayout, Row, Col, Spin} from 'antd'
import {$do, connect, View} from '@tesler-ui/core'
import {AppState} from 'reducers'
import AppSideMenu from 'components/AppSideMenu/AppSideMenu'
import AppBar from 'components/AppBar/AppBar'
import ViewHeader from 'components/ViewHeader/ViewHeader'
import * as styles from './AppLayout.less'
import {SystemNotification} from '@tesler-ui/core/interfaces/objectMap'
import Card from 'components/Card/Card'
import {WidgetMeta, WidgetTypes} from '@tesler-ui/core/interfaces/widget'
import Login from 'components/Login/Login'
import {ApplicationError} from '@tesler-ui/core/interfaces/view'
import {setMenuVisible, setMobileMenu} from 'actions/actions'

interface LayoutProps {
    screenName: string,
    sessionActive: boolean,
    savedSessionActive: boolean,
    widgets: WidgetMeta[],
    systemNotifications: SystemNotification[],
    error: ApplicationError,
    loginError: string,
    menuVisible: boolean,
    mobileMenu: boolean,
    onLogin: () => void,
    onNotificationClose: (id: number) => void,
    onErrorClose: () => void,
    onLogout: () => void,
    onMenuVisible: (menuVisible: boolean) => void,
    onMobileMenu: (mobileMenu: boolean) => void
}

const skipWidgetTypes = [
    WidgetTypes.HeaderWidget,
    WidgetTypes.SecondLevelMenu,
]

export function Layout(props: LayoutProps) {
    const isInfoPanelLayout = props.widgets.some(widget => widget.type !== WidgetTypes.List)
    const headerWidth = {
        width: isInfoPanelLayout ? '1138px' : '1152px',
        maxWidth: '100%'
    }
    const bodyWidth = {
        width: isInfoPanelLayout ? '802px' : '1152px',
        maxWidth: '100%'
    }

    React.useEffect(() => {
        if (props.savedSessionActive) {
            props.onLogin()
        }
    }, [])

    React.useEffect(() => {
        const isMobileWidth = window.matchMedia('(max-width: 802px)').matches
        props.onMobileMenu(isMobileWidth)

        if (isMobileWidth) {
            props.onMenuVisible(false)
        }

        const resizeListener = () => {
            if (props.mobileMenu !== window.matchMedia('(max-width: 802px)').matches) {
                props.onMobileMenu(window.matchMedia('(max-width: 802px)').matches)
            }

        }

        window.addEventListener('resize', resizeListener)

        return () => {
            window.removeEventListener('resize', resizeListener)
        }
    }, [props.mobileMenu])

    return !props.sessionActive
        ? <div className={styles.spinContainer}>
            <div className={styles.spinCard}>
                {(props.savedSessionActive)
                    ? <Spin />
                    : <Login />
                }
            </div>
        </div>
        : <div className={styles.Container}>
        <AntLayout>
            <AntLayout.Sider
                    className={styles.sideMenu}
                    theme="dark"
                    width={
                        props.mobileMenu
                        ? props.menuVisible ? 48 : 0
                        : props.menuVisible ? 256 : 48
                    }
                >
                <AppSideMenu onMenuVisible={props.onMenuVisible}/>
            </AntLayout.Sider>
            <AntLayout className={styles.affixTargetWrapper}>
                <AntLayout.Content>
                    <AntLayout.Header>
                        <div className={styles.headerWrapper}>
                            <AppBar
                                headerWidth={headerWidth}
                                onMenuVisible={props.onMenuVisible}
                            />
                        </div>
                    </AntLayout.Header>
                    <AntLayout.Content>
                        <Row>
                            <ViewHeader
                                headerWidth={headerWidth}
                            />
                        </Row>
                        <Row type="flex" justify="center">
                            <Col style={headerWidth}>
                                <div style={bodyWidth}>
                                    <View
                                        card={Card as any}
                                        skipWidgetTypes={skipWidgetTypes}
                                    />
                                </div>
                            </Col>
                        </Row>
                    </AntLayout.Content>
                </AntLayout.Content>
            </AntLayout>
        </AntLayout>
    </div>
}

function mapStateToProps(store: AppState) {
    return {
        sessionActive: store.session.active,
        savedSessionActive: store.session.savedSessionActive,
        screenName: store.router.screenName,
        widgets: store.view.widgets,
        menuVisible: store.screen.menuVisible,
        mobileMenu: store.screen.mobileMenu,
        systemNotifications: store.view.systemNotifications,
        error: store.view.error,
        loginError: store.session.errorMsg
    }
}

function mapDispatchToProps(dispatch: Dispatch) {
    return {
        onLogin: () => {
            dispatch($do.login(null))
        },
        onLogout: () => {
            dispatch($do.logout(null))
        },
        onNotificationClose: (id: number) => {
            dispatch($do.closeNotification({ id }))
        },
        onErrorClose: () => {
            dispatch($do.closeViewError(null))
        },
        onMenuVisible: (menuVisible: boolean) => {
            dispatch(setMenuVisible(menuVisible))
        },
        onMobileMenu: (mobileMenu: boolean) => {
            dispatch(setMobileMenu(mobileMenu))
        }
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(Layout)
