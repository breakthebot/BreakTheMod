/*
 * This file is part of breakthemod.
 *
 * breakthemod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * breakthemod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with breakthemod. If not, see <https://www.gnu.org/licenses/>.
 */

package net.charisk.breakthemod.Services;


import net.charisk.breakthemod.Fetch.types.Resident;
import net.charisk.breakthemod.api.Fetch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class townlessService {
    private static final int BATCH_SIZE = 100;
    private static final Logger LOGGER = LoggerFactory.getLogger("breakthemod");
    private final Fetch fetcher = new Fetch();

    public List<String> get(List<?> onlinePlayers) {
        List<String> ids = onlinePlayers.stream()
                .map(id -> id.toString())
                .collect(Collectors.toList());
        try {
            if (ids.size() == 1) {
                Resident resident = fetcher.getResident(ids.get(0));
                if (resident != null && resident.getTown() != null) {
                    return Collections.singletonList(resident.getTown().get().getName().get());
                }
                return null;
            }

            List<String> townless = new ArrayList<>();
            for (int i = 0; i < ids.size(); i += BATCH_SIZE) {
                int end = Math.min(i + BATCH_SIZE, ids.size());
                List<String> batch = ids.subList(i, end);
                List<Resident> residents = fetcher.getResidents(batch);

                townless.addAll(residents.stream()
                        .filter(res -> res.getStatus() != null && !res.getStatus().get().getHasTown().get())
                        .map(Resident::getName)
                        .collect(Collectors.toList())
                );
            }
            return townless;

        } catch (Exception e) {
            LOGGER.error("Error fetching town data for {}: {}", onlinePlayers, e.getMessage(), e);
            return null;
        }
    }
}