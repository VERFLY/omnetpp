//==========================================================================
//  DATASORTER.CC - part of
//                     OMNeT++/OMNEST
//            Discrete System Simulation in C++
//
//  Author: Andras Varga, Tamas Borbely
//
//==========================================================================

/*--------------------------------------------------------------*
  Copyright (C) 1992-2008 Andras Varga
  Copyright (C) 2006-2008 OpenSim Ltd.

  This file is distributed WITHOUT ANY WARRANTY. See the file
  `license' for details on this and other legal matters.
*--------------------------------------------------------------*/

#include <algorithm>
#include <utility>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include "resultfilemanager.h"
#include "commonutil.h"
#include "datasorter.h"
#include "stringutil.h"

USING_NAMESPACE

using namespace std;


ResultFileManager *ScalarDataSorter::tmpScalarMgr;

/*----------------------------------------
 *              XYDataset
 *----------------------------------------*/
void XYDataset::add(const ScalarResult &d)
{
    int row, column;

    KeyToIndexMap::iterator rowRef = rowKeyToIndexMap.find(d);
    if (rowRef != rowKeyToIndexMap.end())
    {
        row = rowOrder[rowRef->second];
    }
    else // add new row
    {
        row = rowKeys.size();
        values.push_back(vector<Statistics>(columnKeys.size(), Statistics()));
        rowKeyToIndexMap[d] = row;
        rowKeys.push_back(d);
        rowOrder.push_back(row);
    }

    KeyToIndexMap::iterator columnRef = columnKeyToIndexMap.find(d);
    if (columnRef != columnKeyToIndexMap.end())
    {
        column = columnOrder[columnRef->second];
    }
    else // add new column
    {
        column = columnKeys.size();
        for (vector<Row>::iterator rowRef = values.begin(); rowRef != values.end(); ++rowRef)
            rowRef->push_back(Statistics());
        columnKeyToIndexMap[d] = column;
        columnKeys.push_back(d);
        columnOrder.push_back(column);
    }

    values.at(row).at(column).collect(d.value);
}

void XYDataset::swapRows(int row1, int row2)
{
    int temp = rowOrder[row1];
    rowOrder[row1] = rowOrder[row2];
    rowOrder[row2] = temp;
}

void XYDataset::sortRows()
{
    vector<int> rowOrder;
    for (KeyToIndexMap::iterator it = rowKeyToIndexMap.begin(); it != rowKeyToIndexMap.end(); ++it)
        rowOrder.push_back(this->rowOrder[it->second]);

    this->rowOrder = rowOrder;
}

void XYDataset::sortColumns()
{
    vector<int> columnOrder;
    for (KeyToIndexMap::iterator it = columnKeyToIndexMap.begin(); it != columnKeyToIndexMap.end(); ++it)
        columnOrder.push_back(this->columnOrder[it->second]);
    this->columnOrder = columnOrder;
}

struct ValueAndIndex
{
    double value;
    int index;
    ValueAndIndex(double value, int index) : value(value), index(index) {}
    bool operator<(const ValueAndIndex& other) const { return this->value < other.value; }
};

void XYDataset::sortColumnsAccordingToFirstRowMean()
{
    if (values.size() > 0)
    {
        vector<ValueAndIndex> vals;
        int firstRow = rowOrder[0];
        for (int i = 0; i < (int)values[firstRow].size(); ++i)
        {
            double mean = values[firstRow][i].getMean();
            if (!isNaN(mean))
                vals.push_back(ValueAndIndex(mean, i));
        }

        sort(vals.begin(), vals.end());

        columnOrder = vector<int>(vals.size());
        for (int i = 0; i < (int)vals.size(); ++i)
            columnOrder[i] = vals[i].index;
    }
}

/*
 * Grouping functions
 */
bool ScalarDataSorter::sameGroupFileRunScalar(const ScalarResult& d1, const ScalarResult& d2)
{
    return d1.fileRunRef==d2.fileRunRef && d1.nameRef==d2.nameRef;
}

bool ScalarDataSorter::sameGroupModuleScalar(const ScalarResult& d1, const ScalarResult& d2)
{
    return d1.moduleNameRef==d2.moduleNameRef && d1.nameRef==d2.nameRef;
}

bool ScalarDataSorter::sameGroupFileRunModule(const ScalarResult& d1, const ScalarResult& d2)
{
    return d1.fileRunRef==d2.fileRunRef && d1.moduleNameRef==d2.moduleNameRef;
}

/*
 * Compare functions
 */
bool ScalarDataSorter::lessByModuleRef(ID id1, ID id2)
{
    if (id1==-1 || id2==-1) return id2!=-1; // -1 is the smallest
    const ScalarResult& d1 = tmpScalarMgr->getScalar(id1);
    const ScalarResult& d2 = tmpScalarMgr->getScalar(id2);
    return strdictcmp(d1.moduleNameRef->c_str(), d2.moduleNameRef->c_str()) < 0;
}

bool ScalarDataSorter::equalByModuleRef(ID id1, ID id2)
{
    if (id1==-1 || id2==-1) return id1==id2;
    const ScalarResult& d1 = tmpScalarMgr->getScalar(id1);
    const ScalarResult& d2 = tmpScalarMgr->getScalar(id2);
    return d1.moduleNameRef == d2.moduleNameRef;
}

bool ScalarDataSorter::lessByFileAndRun(ID id1, ID id2)
{
    if (id1==-1 || id2==-1) return id2!=-1; // -1 is the smallest
    const ScalarResult& d1 = tmpScalarMgr->getScalar(id1);
    const ScalarResult& d2 = tmpScalarMgr->getScalar(id2);

    // compare first by file, then by run
    int cmpFile = strdictcmp(d1.fileRunRef->fileRef->filePath.c_str(), d2.fileRunRef->fileRef->filePath.c_str());
    if (cmpFile!=0)
        return cmpFile < 0;
    int cmpRun = strdictcmp(d1.fileRunRef->runRef->runName.c_str(), d2.fileRunRef->runRef->runName.c_str());
    return cmpRun < 0;
}

bool ScalarDataSorter::equalByFileAndRun(ID id1, ID id2)
{
    if (id1==-1 || id2==-1) return id1==id2;
    const ScalarResult& d1 = tmpScalarMgr->getScalar(id1);
    const ScalarResult& d2 = tmpScalarMgr->getScalar(id2);
    return d1.fileRunRef == d2.fileRunRef;
}

bool ScalarDataSorter::lessByScalarNameRef(ID id1, ID id2)
{
    if (id1==-1 || id2==-1) return id2!=-1; // -1 is the smallest
    const ScalarResult& d1 = tmpScalarMgr->getScalar(id1);
    const ScalarResult& d2 = tmpScalarMgr->getScalar(id2);
    return strdictcmp(d1.nameRef->c_str(), d2.nameRef->c_str()) < 0;
}

bool ScalarDataSorter::equalByScalarNameRef(ID id1, ID id2)
{
    if (id1==-1 || id2==-1) return id1==id2;
    const ScalarResult& d1 = tmpScalarMgr->getScalar(id1);
    const ScalarResult& d2 = tmpScalarMgr->getScalar(id2);
    return d1.nameRef == d2.nameRef;
}

bool ScalarDataSorter::lessByValue(ID id1, ID id2)
{
    if (id1==-1 || id2==-1) return id2!=-1; // -1 is the smallest
    const ScalarResult& d1 = tmpScalarMgr->getScalar(id1);
    const ScalarResult& d2 = tmpScalarMgr->getScalar(id2);
    return d1.value < d2.value;
}

/*----------------------------------------
 *             ScalarDataSorter
 *----------------------------------------*/

template<class GroupingFn>
IDVectorVector ScalarDataSorter::doGrouping(const IDList& idlist, GroupingFn sameGroup)
{
    tmpScalarMgr = resultFileMgr;

    // parse idlist and do grouping as well, on the fly
    IDVectorVector vv;
    int sz = idlist.size();
    for (int ii = 0; ii < sz; ii++)
    {
        ID id = idlist.get(ii);

        // check of this id shares fileRef, runNumber & scalarName with one of the
        // IDVectors already in vv
        const ScalarResult& d = resultFileMgr->getScalar(id);
        IDVectorVector::iterator i;
        for (i=vv.begin(); i!=vv.end(); ++i)
        {
            ID vvid = (*i)[0];  // first element in IDVector selected by i
            const ScalarResult& vvd = resultFileMgr->getScalar(vvid);
            if (sameGroup(d,vvd))
                break;
        }
        if (i==vv.end())
        {
            // not found -- new one has to be added
            vv.push_back(IDVector());
            i = vv.end()-1;
        }

        // insert
        i->push_back(id);
    }
    return vv;
}

template <class LessFn, class EqualFn>
void ScalarDataSorter::sortAndAlign(IDVectorVector& vv, LessFn less, EqualFn equal)
{
    tmpScalarMgr = resultFileMgr;

    // order each group
    for (IDVectorVector::iterator i=vv.begin(); i!=vv.end(); ++i)
        std::sort(i->begin(), i->end(), less);

    // now insert "null" elements (id=-1) so that every group is of same length,
    // and same indices are "equal()"
    for (int pos=0; ; pos++)
    {
        // determine "smallest" element in all vectors, on position "pos"
        ID minId = -1;
        IDVectorVector::iterator i;
        for (i=vv.begin(); i!=vv.end(); ++i)
            if ((int)i->size()>pos)
                minId = (minId==-1) ? (*i)[pos] : less((*i)[pos],minId) ? (*i)[pos] : minId;

        // if pos is past the end of all vectors, we're done
        if (minId==-1)
            break;

        // if a vector has something different on this position, add a "null" element here
        for (i=vv.begin(); i!=vv.end(); ++i)
            if ((int)i->size()<=pos || !equal((*i)[pos],minId))
                i->insert(i->begin()+pos,-1);
    }
}

IDVectorVector ScalarDataSorter::groupByRunAndName(const IDList& idlist)
{
    // form groups (IDVectors) by fileRef+runNumber+scalarName
    IDVectorVector vv = doGrouping(idlist, sameGroupFileRunScalar);

    // order each group by module name, and insert "null" elements (id=-1) so that
    // every group is of same length, and same indices contain same moduleNameRefs
    sortAndAlign(vv, lessByModuleRef, equalByModuleRef);

    return vv;
}

IDVectorVector ScalarDataSorter::groupByModuleAndName(const IDList& idlist)
{
    // form groups (IDVectors) by moduleName+scalarName
    IDVectorVector vv = doGrouping(idlist, sameGroupModuleScalar);

    // order each group by fileRef+runNumber, and insert "null" elements (id=-1) so that
    // every group is of same length, and same indices contain same fileRef+runNumber
    sortAndAlign(vv, lessByFileAndRun, equalByFileAndRun);

    return vv;
}

IDVectorVector ScalarDataSorter::groupByFields(const IDList& idlist, ResultItemFields fields)
{
    IDVectorVector vv = doGrouping(idlist, ResultItemFieldsEqual(fields));

    sortAndAlign(vv,
        IDFieldsLess(fields.complement(), resultFileMgr),
        IDFieldsEqual(fields.complement(), resultFileMgr));
    return vv;
}

/*
 * Returns true iff the jth column is missing
 * i.e. X value (i=0) is missing or each Y value (i>0) is missing.
 */
static bool isMissing(const IDVectorVector &vv, int j)
{
    if (vv[0][j]==-1)
        return true;
    bool foundY = false;
    for (int i=1; i<(int)vv.size();++i)
        if (vv[i][j]!=-1)
        {
            foundY = true;
            break;
        }
    return !foundY;
}

XYDataset ScalarDataSorter::groupAndAggregate(const IDList& idlist, ResultItemFields rowFields, ResultItemFields columnFields)
{
    XYDataset dataset(rowFields, columnFields);

    int sz = idlist.size();
    for (int i = 0; i < sz; i++)
    {
        ID id = idlist.get(i);
        const ScalarResult& d = resultFileMgr->getScalar(id);
        dataset.add(d);
    }
    return dataset;
}


IDVectorVector ScalarDataSorter::prepareScatterPlot(const IDList& idlist, const char *moduleName, const char *scalarName)
{
    // form groups (IDVectors) by moduleName+scalarName
    IDVectorVector vv = doGrouping(idlist, sameGroupModuleScalar);
    if (vv.size()==0)
        return vv;

    // order each group by fileRef+runNumber, and insert "null" elements (id=-1) so that
    // every group is of same length, and same indices contain same fileRef+runNumber
    sortAndAlign(vv, lessByFileAndRun, equalByFileAndRun);

    // find series for X axis (modulename, scalarname)...
    int xpos = -1;
    for (IDVectorVector::iterator i=vv.begin(); i!=vv.end(); ++i)
    {
        ID id = -1;
        for (IDVector::iterator j=i->begin(); j!=i->end(); ++j)
            if (*j!=-1)
                {id = *j;break;}
        if (id==-1)
            continue;
        const ScalarResult& d = resultFileMgr->getScalar(id);
        if (*d.moduleNameRef==moduleName && *d.nameRef==scalarName)
            {xpos = i-vv.begin();break;}
    }
    if (xpos==-1)
        throw opp_runtime_error("data for X axis not found");

    // ... and bring X series to 1st place
    if (xpos!=0)
        std::swap(vv[0], vv[xpos]);

    // sort x axis, moving elements in all other vectors as well.
    // Strategy: we'll construct the result in vv2. First we sort X axis, then
    // move elements of the other vectors to the same positions where the
    // X values went.

    IDVectorVector vv2;
    vv2.resize(vv.size());
    vv2[0] = vv[0];

    // step one: remove values where X value is missing or each Y value is missing (id=-1)
    IDVector::iterator it = vv2[0].begin();
    int sz = vv[0].size();
    for (int j=0; j<sz; ++j)
    {
        if(isMissing(vv,j))
            vv2[0].erase(it);
        else
            ++it;
    }

    // step two: sort X axis
    std::sort(vv2[0].begin(), vv2[0].end(), lessByValue);

    // step three: allocate all other vectors in vv2 to be the same length
    // (necessary because we'll fill them in via assignment, NOT push_back() or insert())
    for (int k=1; k<(int)vv2.size(); k++)
        vv2[k].resize(vv2[0].size());

    // step four: copy over elements
    for (int pos=0; pos<(int)vv[0].size(); pos++)
    {
        ID id = vv[0][pos];
        if (id==-1) continue;
        IDVector::iterator dest = std::find(vv2[0].begin(),vv2[0].end(),id);
        if (dest==vv2[0].end()) continue;
        int destpos = dest - vv2[0].begin();
        for (int k=1; k<(int)vv.size(); k++)
            vv2[k][destpos] = vv[k][pos];
    }

    return vv2;
}

XYDataset ScalarDataSorter::prepareScatterPlot2(const IDList& idlist, const char *moduleName, const char *scalarName,
            ResultItemFields rowFields, ResultItemFields columnFields)
{
    XYDataset dataset = groupAndAggregate(idlist, rowFields, columnFields);

    // find row of x values and move it to row 0
    int row;
    for (row = 0; row < dataset.getRowCount(); ++row)
    {
        std::string moduleName = dataset.getRowField(row, ResultItemField(ResultItemField::MODULE));
        std::string dataName = dataset.getRowField(row, ResultItemField(ResultItemField::NAME));
        if (dataset.getRowField(row, ResultItemField(ResultItemField::MODULE)) == moduleName &&
            dataset.getRowField(row, ResultItemField(ResultItemField::NAME)) == scalarName)
            break;
    }
    if (row < dataset.getRowCount())
    {
        if (row > 0)
            dataset.swapRows(0, row);
    }
    else
        throw opp_runtime_error("Data for X axis not found.");

    // sort columns so that X values are in ascending order
    dataset.sortColumnsAccordingToFirstRowMean();

    return dataset;
}

struct IsoGroupingFn : public std::binary_function<ResultItem, ResultItem, bool>
{
    typedef map<Run*, vector<double> > RunIsoValueMap;
    typedef map<pair<string, string>, int> IsoAttrIndexMap;
    RunIsoValueMap isoMap;
    ResultItemFields fields;

    IsoGroupingFn() {}
    IDList init(const IDList &idlist, StringVector moduleNames, StringVector scalarNames,
				ResultItemFields fields, ResultFileManager *manager);
    bool operator()(const ResultItem &d1, const ResultItem &d2) const;
};

IDList IsoGroupingFn::init(const IDList &idlist, StringVector moduleNames, StringVector scalarNames,
							ResultItemFields fields, ResultFileManager *manager)
{
	this->fields = fields;

    //assert(moduleNames.size() == scalarNames.size());
    int numOfIsoValues = scalarNames.size();
    IDList result;

    // build iso (module,scalar) -> index map
    IsoAttrIndexMap indexMap;
    for (int i = 0; i < numOfIsoValues; ++i)
    {
        pair<string,string> key = make_pair(moduleNames[i], scalarNames[i]);
        indexMap[key] = i;
    }

    // build run -> iso values map
    int sz = idlist.size();
    for (int i = 0; i < sz; ++i)
    {
        ID id = idlist.get(i);
        const ScalarResult &scalar = manager->getScalar(id);
        pair<string,string> key = make_pair(*scalar.moduleNameRef, *scalar.nameRef);
        IsoAttrIndexMap::iterator it = indexMap.find(key);
        if (it != indexMap.end())
        {
            int index = it->second;
            Run *run = scalar.fileRunRef->runRef;
            RunIsoValueMap::iterator it2 = isoMap.find(run);
            if (it2 == isoMap.end())
            {
                it2 = isoMap.insert(make_pair(run, vector<double>(numOfIsoValues, dblNaN))).first;
            }
            it2->second[index] = scalar.value;
        }
        else
            result.add(id);
    }

    return result;
}

bool IsoGroupingFn::operator()(const ResultItem &d1, const ResultItem &d2) const
{
    if (d1.fileRunRef->runRef == d2.fileRunRef->runRef)
        return true;

    if (isoMap.empty())
        return true;

    if (!fields.equal(d1,d2))
    	return false;

    RunIsoValueMap::const_iterator it1 = isoMap.find(d1.fileRunRef->runRef);
    RunIsoValueMap::const_iterator it2 = isoMap.find(d2.fileRunRef->runRef);
    if (it1 == isoMap.end() || it2 == isoMap.end())
        return false;

    const vector<double> &v1 = it1->second;
    const vector<double> &v2 = it2->second;

    assert(v1.size() == v2.size());

    for (int i = 0; i < (int)v1.size(); ++i)
        if(v1[i] != v2[i])
            return false;

    return true;
}

XYDatasetVector ScalarDataSorter::prepareScatterPlot3(const IDList& idlist, const char *moduleName, const char *scalarName,
        ResultItemFields rowFields, ResultItemFields columnFields,
        const StringVector isoModuleNames, const StringVector isoScalarNames, ResultItemFields isoFields)
{
    // group data according to iso fields
    IsoGroupingFn grouping;
    IDList nonIsoScalars = grouping.init(idlist, isoModuleNames, isoScalarNames, isoFields, resultFileMgr);
    IDVectorVector groupedScalars = doGrouping(nonIsoScalars, grouping);

    XYDatasetVector datasets;
    for (IDVectorVector::iterator group = groupedScalars.begin(); group != groupedScalars.end(); ++group)
    {
        IDList groupAsIDList;
        for (IDVector::iterator id = group->begin(); id != group->end(); id++)
            groupAsIDList.add(*id);

        try
        {
            XYDataset dataset = prepareScatterPlot2(groupAsIDList, moduleName, scalarName, rowFields, columnFields);
            datasets.push_back(dataset);
        }
        catch (opp_runtime_error &e) {
            // no X data for some iso values -> omit from the result
        }
    }

    return datasets;
}

IDList ScalarDataSorter::getModuleAndNamePairs(const IDList& idlist, int maxcount)
{
    IDList out;

    // go through idlist and pick ids that represent a new (module, name pair)
    for (int ii = 0; ii < (int)idlist.size(); ii++)
    {
        ID id = idlist.get(ii);

        // check if module and name of this id is already in out[]
        const ScalarResult& d = resultFileMgr->getScalar(id);
        int i;
        int outSize = out.size();
        for (i=0; i<outSize; i++)
        {
            const ScalarResult& vd = resultFileMgr->getScalar(out.get(i));
            if (d.moduleNameRef==vd.moduleNameRef && d.nameRef==vd.nameRef)
                break;
        }

        // not yet -- then add it
        if (i==outSize)
        {
            out.add(id);
            if (outSize>maxcount)
                break; // enough is enough
        }
    }

    return out;
}

IDVectorVector ScalarDataSorter::prepareCopyToClipboard(const IDList& idlist)
{
    // form groups (IDVectors) by fileRef+runNumber+moduleNameRef
    IDVectorVector vv = doGrouping(idlist, sameGroupFileRunModule);

    // order each group by scalar name, and insert "null" elements (id=-1) so that
    // every group is of same length, and same indices contain same scalarNameRefs
    sortAndAlign(vv, lessByScalarNameRef, equalByScalarNameRef);

    return vv;
}

