/*
 * Copyright 2011 the original author or authors.
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
package org.gradle.api.internal.artifacts.repositories;

import org.apache.ivy.core.module.descriptor.DependencyDescriptor;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.resolve.ResolveData;
import org.apache.ivy.core.resolve.ResolvedModuleRevision;
import org.apache.ivy.plugins.resolver.IBiblioResolver;
import org.gradle.api.internal.artifacts.ivyservice.dynamicversions.ForceChangeDependencyDescriptor;

import java.text.ParseException;
import java.util.Collections;

public class MavenResolver extends IBiblioResolver {
    public MavenResolver() {
        setChangingPattern(null);
    }

    @Override
    public ResolvedModuleRevision getDependency(DependencyDescriptor dd, ResolveData data) throws ParseException {
        if (dd.getDependencyRevisionId().getRevision().endsWith("SNAPSHOT")) {
            // Force resolution with changing flag set
            DependencyDescriptor changingDescriptor = ForceChangeDependencyDescriptor.forceChangingFlag(dd, true);

            ResolvedModuleRevision changingModule = super.getDependency(changingDescriptor, data);

            // Add flag to module indicating that it is changing
            ModuleRevisionId resolvedId = changingModule.getDescriptor().getResolvedModuleRevisionId();
            ModuleRevisionId changingId =
                    ModuleRevisionId.newInstance(resolvedId.getOrganisation(), resolvedId.getName(), resolvedId.getRevision(),
                            Collections.singletonMap("CHANGING_MODULE", "TRUE"));
            changingModule.getDescriptor().setResolvedModuleRevisionId(changingId);

            return changingModule;
        }
        return super.getDependency(dd, data);
    }
}
