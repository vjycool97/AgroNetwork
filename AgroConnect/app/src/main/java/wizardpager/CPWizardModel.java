/*
 * Copyright 2012 Roman Nurik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wizardpager;

import android.content.Context;

import com.tech.freak.wizardpager.model.AbstractWizardModel;
import com.tech.freak.wizardpager.model.Page;
import com.tech.freak.wizardpager.model.PageList;

import java.util.ArrayList;

import agroconnectapp.agroconnect.in.agroconnect.NetworkController;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.model.CPNode;
import wizardpager.pages.CPChoicePage;

public class CPWizardModel extends AbstractWizardModel {
    public CPWizardModel(Context context) {
        super(context);
    }


    @Override
    protected PageList onNewRootPageList() {
        CPNode node = new CPNode();
        node.setChildrenCropList(new ArrayList<CPNode>());

        for(CPNode cpNode : NetworkController.getInstance().mCPNodes)
            node.getChildrenCropList().add(cpNode);

        Page page = new CPChoicePage(this, "1. Crop Protection").setChoices(node).setRequired(true);
        return new PageList(page);
    }


}
