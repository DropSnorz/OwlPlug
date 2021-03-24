/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.owlplug.auth.controllers

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXSpinner
import com.owlplug.auth.services.AuthenticationService
import com.owlplug.core.components.LazyViewRegistry
import com.owlplug.core.controllers.MainController
import com.owlplug.core.controllers.dialogs.AbstractDialogController
import javafx.concurrent.Task
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller

@Controller
class AccountController : AbstractDialogController() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var authenticationService: AuthenticationService

    @Autowired
    private lateinit var mainController: MainController

    @Autowired
    private lateinit var viewRegistry: LazyViewRegistry

    @FXML
    private lateinit var buttonPane: HBox

    @FXML
    private lateinit var googleButton: JFXButton

    @FXML
    private lateinit var authProgressIndicator: JFXSpinner

    @FXML
    private lateinit var messageLabel: Label

    @FXML
    private lateinit var cancelButton: JFXButton

    @FXML
    private lateinit var closeButton: JFXButton

    /** Indicates if the user has pressed the cancel button.  */
    private var cancelFlag = false

    /**
     * FXML initialize method.
     */
    fun initialize() {
        googleButton.onAction = EventHandler {
            buttonPane.isVisible = false
            authProgressIndicator.isVisible = true
            messageLabel.text = "Your default browser is opening... Proceed to sign in and come back here."
            messageLabel.isVisible = true
            val task: Task<Void> = object : Task<Void>() {
                @Throws(Exception::class)
                override fun call(): Void? {
                    log.debug("Google auth task started")
                    authenticationService.createAccountAndAuth()
                    this.updateProgress(1, 1)
                    return null
                }
            }
            task.onSucceeded = EventHandler {
                log.debug("Google auth task complete")
                authProgressIndicator.isVisible = false
                buttonPane.isVisible = false
                cancelButton.isVisible = false
                closeButton.isVisible = true
                messageLabel.text = "Your account has been successfully added"
                messageLabel.isVisible = true
                cancelFlag = false
                mainController.refreshAccounts()
            }
            task.onFailed = EventHandler {
                log.debug("Google auth task failed")
                authProgressIndicator.isVisible = false
                buttonPane.isVisible = true
                cancelButton.isVisible = false
                closeButton.isVisible = true
                messageLabel.isVisible = false
                if (!cancelFlag) {
                    messageLabel.text = "En error occured during authentification"
                    messageLabel.isVisible = true
                }
                cancelFlag = false
            }
            cancelButton.isVisible = true
            closeButton.isVisible = false
            Thread(task).start()
        }
        cancelButton.onAction = EventHandler {
            cancelFlag = true
            authenticationService.stopAuthReceiver()
        }
        closeButton.onAction = EventHandler { close() }

        // Do not compute layout for invisible nodes
        buttonPane.managedProperty().bind(buttonPane.visibleProperty())
        authProgressIndicator.managedProperty().bind(authProgressIndicator.visibleProperty())
        messageLabel.managedProperty().bind(messageLabel.visibleProperty())
    }

    override fun onDialogShow() {
        buttonPane.isVisible = true
        cancelButton.isVisible = false
        closeButton.isVisible = true
        messageLabel.isVisible = false
    }

    override fun getBody(): Node {
        return viewRegistry[LazyViewRegistry.NEW_ACCOUNT_VIEW]
    }

    override fun getHeading(): Node? {
        return null
    }
}