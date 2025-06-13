/*
 * This file is part of breakthemodrewrite.
 *
 * breakthemodrewrite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * breakthemodrewrite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with breakthemodrewrite. If not, see <https://www.gnu.org/licenses/>.
 */

package net.charisk.breakthemodrewrite.Services;


import net.charisk.breakthemodrewrite.Fetch.types.Resident;
import net.charisk.breakthemodrewrite.api.Fetch;
import net.charisk.breakthemodrewrite.utils.timestamps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class lastSeenService {
    private final timestamps timestampParser = new timestamps();
    Fetch fetch = new Fetch();
    private static final Logger LOGGER = LoggerFactory.getLogger("breakthemodrewrite");

    public String get(String username){
        try {
            Resident resident = fetch.getResident(username);
            if (resident == null) {
                return String.format("Player '%s' not found", username);
            }

            long lastOnline = resident.getTimestamps().get().getLastOnline().get();
            boolean isOnline = resident.getStatus().get().getIsOnline().get();

            List<Long> offlineParts = timestampParser.parseTimestamp(lastOnline);
            long days = offlineParts.get(0);
            long hours = offlineParts.get(1);
            long minutes = offlineParts.get(2);

            if (!isOnline) {
                return String.format(
                        "%s has been offline for %d days, %d hours, and %d minutes.",
                        username, days, hours, minutes
                );
            } else {
                return String.format(
                        "%s is currently online, for %d days, %d hours, and %d minutes.",
                        username, days, hours, minutes
                );
            }
        } catch (Exception e) {
            LOGGER.error("Error fetching status for '{}': {}", username, e.getMessage(), e);
            return "An error occurred while fetching player status.";
        }

    }
}
