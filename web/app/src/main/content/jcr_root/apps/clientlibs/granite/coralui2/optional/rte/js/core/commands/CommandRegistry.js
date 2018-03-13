/*************************************************************************
*
* ADOBE CONFIDENTIAL
* ___________________
*
*  Copyright 2012 Adobe Systems Incorporated
*  All Rights Reserved.
*
* NOTICE:  All information contained herein is, and remains
* the property of Adobe Systems Incorporated and its suppliers,
* if any.  The intellectual and technical concepts contained
* herein are proprietary to Adobe Systems Incorporated and its
* suppliers and are protected by trade secret or copyright law.
* Dissemination of this information or reproduction of this material
* is strictly forbidden unless prior written permission is obtained
* from Adobe Systems Incorporated.
**************************************************************************/

/**
 * @class CUI.rte.commands.CommandRegistry
 * This class is used to manage commands used for rich text editing. Each command has a
 * respective identifier by which it can be executed
 * {@link CUI.rte.EditorKernel#execCmd}.
 * @since 5.3
 * @private
 */
CUI.rte.commands.CommandRegistry = function() {

    var cmdRegistry = { };

    return {

        /**
         * Registers the specified class as a rich text editing command.
         * <p>
         * Note that a class that extends {@link CUI.rte.Command} can provide/account for
         * multiple commands. In such cases, it is convention that the {@link #command}
         * parameter of the registring call starts with a "_".
         * @param {String} command The command identifier; must start with a "_" if the
         *        specified command class provides multiple commands
         * @param {Function} cls The command class (must extend
         *        {@link CUI.rte.commands.Command})
         */
        register: function(command, cls) {
            cmdRegistry[command] = cls;
        },

        /**
         * <p>Creates an associative array, containing instances of all currently
         * registered commands.</p>
         * <p>The created object may be used by a single {@link CUI.rte.EditorKernel}
         * instance.</p>
         * @return {Object} Associative array of instantiated commands
         */
        createRegisteredCommands: function() {
            var registeredCommands = { };
            for (var cmd in cmdRegistry) {
                if (cmdRegistry.hasOwnProperty(cmd)) {
                    registeredCommands[cmd] = new cmdRegistry[cmd]();
                }
            }
            return registeredCommands;
        }

    };

}();